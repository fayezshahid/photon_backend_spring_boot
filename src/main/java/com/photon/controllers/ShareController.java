package com.photon.controllers;

import com.photon.dtos.SharedImageDTO;
import com.photon.entities.Image;
import com.photon.services.AuthService;
import com.photon.services.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/shares")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<List<SharedImageDTO>> getSharedImages() {
        Long currentUserId = authService.getCurrentUserId();
        List<SharedImageDTO> sharedImages = shareService.getSharedImages(currentUserId);
        return ResponseEntity.ok(sharedImages);
    }

    @PostMapping("/share/{userId}/{imageId}")
    public ResponseEntity<Map<String, String>> shareImage(
            @PathVariable Long userId,
            @PathVariable Long imageId) {

        Long currentUserId = authService.getCurrentUserId();
        String userEmail = shareService.shareImage(userId, imageId, currentUserId);
        return ResponseEntity.ok(Map.of("userEmail", userEmail));
    }

    @DeleteMapping("/unshare/{userId}/{imageId}")
    public ResponseEntity<Map<String, String>> unshareImage(
            @PathVariable Long userId,
            @PathVariable Long imageId) {

        Long currentUserId = authService.getCurrentUserId();
        String userEmail = shareService.unshareImage(userId, imageId, currentUserId);
        return ResponseEntity.ok(Map.of("userEmail", userEmail));
    }

    @DeleteMapping("/remove/{userId}/{imageId}")
    public ResponseEntity<Void> removeSharedImage(
            @PathVariable Long userId,
            @PathVariable Long imageId) {

        Long currentUserId = authService.getCurrentUserId();
        shareService.removeSharedImage(userId, imageId, currentUserId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check/{userId}/{imageId}")
    public ResponseEntity<Long> checkIfImageShared(
            @PathVariable Long userId,
            @PathVariable Long imageId) {

        Long currentUserId = authService.getCurrentUserId();
        Long shareId = shareService.checkIfImageShared(userId, imageId, currentUserId);
        return ResponseEntity.ok(shareId);
    }
}