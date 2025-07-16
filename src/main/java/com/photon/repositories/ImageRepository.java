package com.photon.repositories;

import com.photon.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByUserIdAndInTrashFalseAndArchivedFalse(Long userId);
    List<Image> findByUserIdAndFavouriteTrueAndInTrashFalseAndArchivedFalse(Long userId);
    List<Image> findByUserIdAndArchivedTrueAndInTrashFalse(Long userId);
    List<Image> findByUserIdAndInTrashTrue(Long userId);
}
