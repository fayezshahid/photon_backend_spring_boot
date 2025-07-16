package com.photon.repositories;

import com.photon.entities.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface PairRepository extends JpaRepository<Pair, Long> {

    // Find pairs where current user is sender
    List<Pair> findBySenderIdAndIsAccepted(Long senderId, Boolean isAccepted);

    // Find pairs where current user is receiver
    List<Pair> findByReceiverIdAndIsAccepted(Long receiverId, Boolean isAccepted);

    // Find specific pair between two users
    Optional<Pair> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

    // Find pair where current user is receiver and other user is sender
    Optional<Pair> findByReceiverIdAndSenderId(Long receiverId, Long senderId);

    // Delete pair by sender and receiver
    @Modifying
    @Query("DELETE FROM Pair p WHERE p.sender.id = :senderId AND p.receiver.id = :receiverId")
    void deleteBySenderIdAndReceiverId(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    // Delete pair between two users (bidirectional)
    @Modifying
    @Query("DELETE FROM Pair p WHERE (p.sender.id = :userId1 AND p.receiver.id = :userId2) OR (p.sender.id = :userId2 AND p.receiver.id = :userId1)")
    void deletePairBetweenUsers(@Param("userId1") Long userId1, @Param("userId2") Long userId2);

    // Update pair acceptance status
    @Modifying
    @Query("UPDATE Pair p SET p.isAccepted = :isAccepted WHERE p.receiver.id = :receiverId AND p.sender.id = :senderId")
    void updateAcceptanceStatus(@Param("receiverId") Long receiverId, @Param("senderId") Long senderId, @Param("isAccepted") Boolean isAccepted);

}