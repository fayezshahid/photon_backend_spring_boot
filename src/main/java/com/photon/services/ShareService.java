package com.photon.services;

import com.photon.entities.Image;
import com.photon.entities.Share;
import com.photon.entities.User;
import com.photon.repositories.ImageRepository;
import com.photon.repositories.ShareRepository;
import com.photon.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShareService {

    private final ShareRepository shareRepository;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    public String shareImage(Long userId, Long imageId, Long currentUserId) {
        // Create new share
        User owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("Owner not found"));
        User viewer = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Viewer not found"));
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new RuntimeException("Image not found"));

        Share share = Share.builder()
                .image(image)
                .owner(owner)
                .viewer(viewer)
                .build();

        shareRepository.save(share);

        return userRepository.findEmailById(userId)
                .orElseThrow(() -> new RuntimeException("User email not found"));
    }

    public String unshareImage(Long userId, Long imageId, Long currentUserId) {
        shareRepository.deleteByImageIdAndOwnerIdAndViewerId(imageId, currentUserId, userId);

        return userRepository.findEmailById(userId)
                .orElseThrow(() -> new RuntimeException("User email not found"));
    }

    public void removeSharedImage(Long userId, Long imageId, Long currentUserId) {
        shareRepository.deleteByImageIdAndOwnerIdAndViewerId(imageId, userId, currentUserId);
    }

    @Transactional(readOnly = true)
    public Long checkIfImageShared(Long userId, Long imageId, Long currentUserId) {
        Optional<Share> share = shareRepository.findByImageIdAndOwnerIdAndViewerId(imageId, currentUserId, userId);
        return share.map(Share::getId).orElse(null);
    }
}