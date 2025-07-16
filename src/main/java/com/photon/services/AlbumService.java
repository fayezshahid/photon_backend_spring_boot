package com.photon.services;

import com.photon.entities.Album;
import com.photon.entities.Image;
import com.photon.entities.User;
import com.photon.repositories.AlbumRepository;
import com.photon.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;

    public List<Album> getAlbums(Long userId) {
        return albumRepository.findByUserId(userId);
    }

    public void addAlbum(String name, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Album album = new Album();
        album.setName(name);
        album.setUser(user);
        albumRepository.save(album);
    }

    public void updateAlbum(Long id, String name) {
        albumRepository.findById(id).ifPresent(album -> {
            album.setName(name);
            albumRepository.save(album);
        });
    }

    public void deleteAlbum(Long id) {
        albumRepository.deleteById(id);
    }
}
