package com.photon.controllers;

import com.photon.dtos.ImageDTO;
import com.photon.entities.AlbumImage;
import com.photon.entities.Image;
import com.photon.mappers.ImageMapper;
import com.photon.services.AlbumImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/album-images")
@RequiredArgsConstructor
public class AlbumImageController {

    private final AlbumImageService albumImageService;
    private final ImageMapper imageMapper;

    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addImageToAlbum(
            @RequestParam Long albumId,
            @RequestParam Long imageId
    ) {
        albumImageService.addImageToAlbum(albumId, imageId);
        return ResponseEntity.ok(Map.of("message", "Image added successfully"));
    }

    @DeleteMapping("/remove")
    public ResponseEntity<Map<String, String>> removeImageFromAlbum(
            @RequestParam Long albumId,
            @RequestParam Long imageId
    ) {
        albumImageService.removeImageFromAlbum(albumId, imageId);
        return ResponseEntity.ok(Map.of("message", "Image removed successfully"));
    }

    @GetMapping("/album/{albumId}")
    public List<ImageDTO> getImagesInAlbum(@PathVariable Long albumId) {
        List<Image> images = albumImageService.getImagesInAlbum(albumId);
        return imageMapper.toDTOList(images);
    }
}
