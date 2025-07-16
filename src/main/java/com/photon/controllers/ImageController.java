package com.photon.controllers;

import com.photon.dtos.ImageDTO;
import com.photon.entities.Image;
import com.photon.mappers.ImageMapper;
import com.photon.services.AuthService;
import com.photon.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    private final AuthService authService;
    private final ImageMapper imageMapper;

    @GetMapping
    public List<ImageDTO> getActiveImages() {
        Long currentUserId = authService.getCurrentUserId();
        List<Image> images = imageService.getActiveImages(currentUserId);
        return imageMapper.toDTOList(images);
    }

    @GetMapping("/favourites")
    public List<ImageDTO> getFavoriteImages() {
        Long currentUserId = authService.getCurrentUserId();
        List<Image> images = imageService.getFavoriteImages(currentUserId);
        return imageMapper.toDTOList(images);
    }

    @GetMapping("/archived")
    public List<ImageDTO> getArchivedImages() {
        Long currentUserId = authService.getCurrentUserId();
        List<Image> images = imageService.getArchivedImages(currentUserId);
        return imageMapper.toDTOList(images);
    }

    @GetMapping("/trash")
    public List<ImageDTO> getTrashedImages() {
        Long currentUserId = authService.getCurrentUserId();
        List<Image> images = imageService.getTrashedImages(currentUserId);
        return imageMapper.toDTOList(images);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "name", required = false) String name
    ) {
        try {
            Long currentUserId = authService.getCurrentUserId();
            imageService.addImage(image, name, currentUserId);
            return ResponseEntity.ok(Map.of("message", "Image uploaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Image upload failed: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateImage(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) {
        try {
            imageService.updateImage(id, name, image);
            return ResponseEntity.ok("Image updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Image update failed: " + e.getMessage());
        }
    }

    @PatchMapping("/{id}/trash")
    public ResponseEntity<Map<String, String>> toggleTrash(@PathVariable Long id) {
        boolean isTrashed = imageService.toggleTrash(id);
        String message = isTrashed ? "Image moved to trash" : "Image restored from trash";
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PatchMapping("/{id}/favourite")
    public ResponseEntity<Map<String, String>> toggleFavourite(@PathVariable Long id) {
        boolean isFavourite = imageService.toggleFavourite(id);
        String message = isFavourite ? "Image moved to Favourite" : "Image restored from Favourite";
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<Map<String, String>> toggleArchive(@PathVariable Long id) {
        boolean isArchived = imageService.toggleArchive(id);
        String message = isArchived  ? "Image moved to Archived" : "Image restored from Archived";
        return ResponseEntity.ok(Map.of("message", message));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteImage(@PathVariable Long id) {
        imageService.deleteImage(id);
        return ResponseEntity.ok(Map.of("message", "Image deleted successfully"));
    }

}
