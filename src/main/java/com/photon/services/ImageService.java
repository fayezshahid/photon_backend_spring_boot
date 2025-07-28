package com.photon.services;

import com.photon.entities.Image;
import com.photon.entities.User;
import com.photon.repositories.ImageRepository;
import com.photon.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.lang.Nullable;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    // S3 dependencies (will be null in local profile)
    private final S3Client s3Client;

    public ImageService(ImageRepository imageRepository, UserRepository userRepository, @Nullable S3Client s3Client) {
        this.imageRepository = imageRepository;
        this.userRepository = userRepository;
        this.s3Client = s3Client;
    }

    // Local file storage configuration
    @Value("${image.upload-dir:}")
    private String uploadDir;

    // S3 configuration
    @Value("${aws.s3.bucket-name:}")
    private String bucketName;

    @Value("${aws.s3.folder:}")
    private String s3Folder;

    // Profile-aware flag
    @Value("${storage.type}")
    private String storageType;

    public List<Image> getActiveImages(Long userId) {
        return imageRepository.findByUserIdAndInTrashFalseAndArchivedFalse(userId);
    }

    public List<Image> getFavoriteImages(Long userId) {
        return imageRepository.findByUserIdAndFavouriteTrueAndInTrashFalseAndArchivedFalse(userId);
    }

    public List<Image> getArchivedImages(Long userId) {
        return imageRepository.findByUserIdAndArchivedTrueAndInTrashFalse(userId);
    }

    public List<Image> getTrashedImages(Long userId) {
        return imageRepository.findByUserIdAndInTrashTrue(userId);
    }

    public void addImage(MultipartFile file, String name, Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String fileReference;

        if ("s3".equals(storageType)) {
            fileReference = uploadToS3(file);
        } else {
            fileReference = uploadToLocal(file);
        }

        // Save metadata to DB
        Image image = new Image();
        image.setName(name);
        image.setImage(fileReference);
        image.setSize(file.getSize());
        image.setUser(user);

        imageRepository.save(image);
    }

    public void updateImage(Long id, String name, MultipartFile newImage) throws IOException {
        Optional<Image> optional = imageRepository.findById(id);
        if (optional.isEmpty()) throw new RuntimeException("Image not found");

        Image image = optional.get();

        if (name != null) {
            image.setName(name);
        }

        if (newImage != null && !newImage.isEmpty()) {
            // Delete old file
            String oldFileReference = image.getImage();
            if ("s3".equals(storageType)) {
                deleteFromS3(oldFileReference);
            } else {
                deleteFromLocal(oldFileReference);
            }

            // Upload new file
            String newFileReference;
            if ("s3".equals(storageType)) {
                newFileReference = uploadToS3(newImage);
            } else {
                newFileReference = uploadToLocal(newImage);
            }

            image.setImage(newFileReference);
            image.setSize(newImage.getSize());
        }

        imageRepository.save(image);
    }

    public boolean toggleTrash(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + id));

        image.setInTrash(!image.isInTrash());
        imageRepository.save(image);
        return image.isInTrash();
    }

    public boolean toggleFavourite(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + id));

        image.setFavourite(!image.isFavourite());
        imageRepository.save(image);
        return image.isFavourite();
    }

    public boolean toggleArchive(Long id) {
        Image image = imageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Image not found with id: " + id));

        image.setArchived(!image.isArchived());
        imageRepository.save(image);
        return image.isArchived();
    }

    public void deleteImage(Long id) {
        Optional<Image> imageOpt = imageRepository.findById(id);
        if (imageOpt.isPresent()) {
            Image image = imageOpt.get();
            String fileReference = image.getImage();

            // Delete file based on storage type
            try {
                if ("s3".equals(storageType)) {
                    deleteFromS3(fileReference);
                } else {
                    deleteFromLocal(fileReference);
                }
            } catch (Exception e) {
                System.err.println("Failed to delete file: " + e.getMessage());
            }
        }
        imageRepository.deleteById(id);
    }

    // Local file storage methods
    private String uploadToLocal(MultipartFile file) throws IOException {
        String filename = generateUniqueFilename(file.getOriginalFilename());
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filename; // Return just the filename for local storage
    }

    private void deleteFromLocal(String filename) throws IOException {
        if (filename != null && !filename.isEmpty()) {
            Path filePath = Paths.get(uploadDir, filename);
            Files.deleteIfExists(filePath);
        }
    }

    // S3 storage methods
    private String uploadToS3(MultipartFile file) throws IOException {
        if (s3Client == null) {
            throw new IllegalStateException("S3Client not available. Check your configuration.");
        }

        String filename = generateUniqueFilename(file.getOriginalFilename());
        String s3Key = s3Folder + filename;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return s3Key; // Return the S3 key
        } catch (S3Exception e) {
            throw new IOException("Failed to upload image to S3: " + e.getMessage(), e);
        }
    }

    private void deleteFromS3(String s3Key) throws S3Exception {
        if (s3Client == null) {
            throw new IllegalStateException("S3Client not available. Check your configuration.");
        }

        if (s3Key != null && !s3Key.isEmpty()) {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        }
    }

    // Helper methods
    private String generateUniqueFilename(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }

    public String getImageUrl(String fileReference) {
        if ("s3".equals(storageType)) {
            // Return S3 URL
            return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileReference);
        } else {
            // Return local file URL (you might need to adjust this based on your setup)
            return "/uploads/" + fileReference;
        }
    }
}