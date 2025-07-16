package com.photon.services;

import com.photon.entities.Album;
import com.photon.entities.AlbumImage;
import com.photon.entities.Image;
import com.photon.repositories.AlbumImageRepository;
import com.photon.repositories.AlbumRepository;
import com.photon.repositories.ImageRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlbumImageService {

    private final AlbumImageRepository albumImageRepository;
    private final AlbumRepository albumRepository;
    private final ImageRepository imageRepository;

    public void addImageToAlbum(Long albumId, Long imageId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        AlbumImage albumImage = new AlbumImage(album, image);
        albumImageRepository.save(albumImage);
    }

    @Transactional
    public void removeImageFromAlbum(Long albumId, Long imageId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new EntityNotFoundException("Image not found"));

        albumImageRepository.deleteByAlbumAndImage(album, image);
    }

    public List<Image> getImagesInAlbum(Long albumId) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new EntityNotFoundException("Album not found"));

        List<AlbumImage> albumImages = albumImageRepository.findByAlbum(album);

        return albumImages.stream()
                .map(AlbumImage::getImage)
                .toList(); // or .collect(Collectors.toList()) if you're using Java 8
    }
}
