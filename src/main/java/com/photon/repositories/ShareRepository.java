package com.photon.repositories;

import com.photon.entities.Share;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ShareRepository extends JpaRepository<Share, Long> {
    Optional<Share> findByImageIdAndOwnerIdAndViewerId(Long imageId, Long ownerId, Long viewerId);

    void deleteByImageIdAndOwnerIdAndViewerId(Long imageId, Long ownerId, Long viewerId);

    @Query("SELECT s.image.id FROM Share s WHERE s.viewer.id = :viewerId")
    List<Long> findImageIdsByViewerId(@Param("viewerId") Long viewerId);

    boolean existsByImageIdAndOwnerIdAndViewerId(Long imageId, Long ownerId, Long viewerId);
}
