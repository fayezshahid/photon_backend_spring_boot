package com.photon.controllers;

import com.photon.entities.Image;
import com.photon.services.AuthService;
import com.photon.services.ShareService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shares")
@RequiredArgsConstructor
public class ShareController {

    private final ShareService shareService;
    private final AuthService authService;

    @PostMapping("/share/{userId}/{imageId}")
    public ResponseEntity<String> shareImage(
            @PathVariable Long userId,
            @PathVariable Long imageId) {

        Long currentUserId = authService.getCurrentUserId();
        String userEmail = shareService.shareImage(userId, imageId, currentUserId);
        return ResponseEntity.ok(userEmail);
    }

    @DeleteMapping("/unshare/{userId}/{imageId}")
    public ResponseEntity<String> unshareImage(
            @PathVariable Long userId,
            @PathVariable Long imageId) {

        Long currentUserId = authService.getCurrentUserId();
        String userEmail = shareService.unshareImage(userId, imageId, currentUserId);
        return ResponseEntity.ok(userEmail);
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