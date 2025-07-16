package com.photon.controllers;

import com.photon.dtos.AlbumDTO;
import com.photon.entities.Album;
import com.photon.entities.Image;
import com.photon.mappers.AlbumMapper;
import com.photon.services.AlbumService;
import com.photon.services.AuthService;
import com.photon.services.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/albums")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final AuthService authService;
    private final AlbumMapper albumMapper;

    @GetMapping
    public List<AlbumDTO> getAlbums() {
        Long userId = authService.getCurrentUserId();
        List<Album> albums = albumService.getAlbums(userId);
        return albumMapper.toDTOList(albums);
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> addAlbum(
            @RequestParam(value = "name") String name
    ) {
        try {
            Long userId = authService.getCurrentUserId();
            albumService.addAlbum(name, userId);
            return ResponseEntity.ok(Map.of("message", "Album added"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateAlbum(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name
    ) {
        try {
            albumService.updateAlbum(id, name);
            return ResponseEntity.ok(Map.of("message", "Album updated"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteAlbum(
            @PathVariable Long id
    ) {
        try {
            albumService.deleteAlbum(id);
            return ResponseEntity.ok(Map.of("message", "Album deleted"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", e.getMessage()));
        }
    }
}
