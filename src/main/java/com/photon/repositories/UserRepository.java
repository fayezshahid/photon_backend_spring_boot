package com.photon.repositories;

import com.photon.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u.email FROM User u WHERE u.id = :userId")
    Optional<String> findEmailById(@Param("userId") Long userId);

    @Query("SELECT u FROM User u WHERE u.id NOT IN :excludedIds")
    List<User> findUsersNotInList(@Param("excludedIds") List<Long> excludedIds);

    @Query("SELECT u FROM User u WHERE u.id IN :includedIds")
    List<User> findUsersByIds(@Param("includedIds") List<Long> includedIds);
}
