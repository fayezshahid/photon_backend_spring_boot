package com.photon.repositories;

import com.photon.entities.AlbumImage;
import com.photon.entities.Album;
import com.photon.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumImageRepository extends JpaRepository<AlbumImage, Void> {
    List<AlbumImage> findByAlbum(Album album);
    void deleteByAlbumAndImage(Album album, Image image);
}
