package com.photon.services;

import com.photon.entities.Image;
import com.photon.entities.User;
import com.photon.repositories.ImageRepository;
import com.photon.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final ImageRepository imageRepository;
    private final UserRepository userRepository;

    @Value("${image.upload-dir}")
    private String uploadDir;

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

        // Save file to disk
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Save metadata to DB
        Image image = new Image();
        image.setName(name);
        image.setImage(filename);
        image.setSize(file.getSize());
        image.setUser(user);

        imageRepository.save(image);
    }

    public void updateImage(Long id, String name, MultipartFile newImage) throws IOException {
        Optional<Image> optional = imageRepository.findById(id);
        if (optional.isEmpty()) throw new RuntimeException("Image not found");

        Image image = optional.get();

        if (name != null) image.setName(name);

        if (newImage != null && !newImage.isEmpty()) {
            // Delete old image file from disk
            String oldImageName = image.getImage();
            Path oldPath = Paths.get(uploadDir, oldImageName);
            Files.deleteIfExists(oldPath);

            // Save new image to disk
            String filename = UUID.randomUUID() + "_" + newImage.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            Path filePath = uploadPath.resolve(filename);
            Files.copy(newImage.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            image.setImage(filename);
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
        imageRepository.deleteById(id);
    }

}
