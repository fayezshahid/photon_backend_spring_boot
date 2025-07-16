package com.photon.controllers;

import com.photon.entities.User;
import com.photon.repositories.UserRepository;
import com.photon.services.AuthService;
import com.photon.services.PairService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/pairs")
@RequiredArgsConstructor
public class PairController {

    private final PairService pairService;
    private final AuthService authService;

    /**
     * Get available users (not friends, no pending requests)
     */
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        Long currentUserId = authService.getCurrentUserId();
        List<User> users = pairService.getAvailableUsers(currentUserId);
        return ResponseEntity.ok(users);
    }

    /**
     * Get friends
     */
    @GetMapping("/friends")
    public ResponseEntity<List<User>> getFriends() {
        Long currentUserId = authService.getCurrentUserId();
        List<User> friends = pairService.getFriends(currentUserId);
        return ResponseEntity.ok(friends);
    }

    /**
     * Get pending friend requests
     */
    @GetMapping("/requests")
    public ResponseEntity<List<User>> getRequests() {
        Long currentUserId = authService.getCurrentUserId();
        List<User> requests = pairService.getPendingRequests(currentUserId);
        return ResponseEntity.ok(requests);
    }

    /**
     * Get sent request IDs
     */
    @GetMapping("/requests/sent")
    public ResponseEntity<List<Long>> getRequestsSent() {
        Long currentUserId = authService.getCurrentUserId();
        List<Long> sentRequestIds = pairService.getSentRequestIds(currentUserId);
        return ResponseEntity.ok(sentRequestIds);
    }

    /**
     * Send friend request
     */
    @PostMapping("/request/{id}")
    public ResponseEntity<String> sendRequest(@PathVariable Long id) {
        try {
            Long currentUserId = authService.getCurrentUserId();
            pairService.sendRequest(currentUserId, id);
            return ResponseEntity.ok("Friend request sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error sending friend request: " + e.getMessage());
        }
    }

    /**
     * Delete sent friend request
     */
    @DeleteMapping("/request/{id}")
    public ResponseEntity<String> deleteRequest(@PathVariable Long id) {
        try {
            Long currentUserId = authService.getCurrentUserId();
            pairService.deleteRequest(currentUserId, id);
            return ResponseEntity.ok("Friend request deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error deleting friend request: " + e.getMessage());
        }
    }

    /**
     * Accept friend request
     */
    @PutMapping("/request/{id}/accept")
    public ResponseEntity<String> acceptRequest(@PathVariable Long id) {
        try {
            Long currentUserId = authService.getCurrentUserId();
            pairService.acceptRequest(currentUserId, id);
            return ResponseEntity.ok("Friend request accepted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error accepting friend request: " + e.getMessage());
        }
    }

    /**
     * Reject friend request
     */
    @DeleteMapping("/request/{id}/reject")
    public ResponseEntity<String> rejectRequest(@PathVariable Long id) {
        try {
            Long currentUserId = authService.getCurrentUserId();
            pairService.rejectRequest(currentUserId, id);
            return ResponseEntity.ok("Friend request rejected successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error rejecting friend request: " + e.getMessage());
        }
    }

    /**
     * Remove friend
     */
    @DeleteMapping("/friend/{id}")
    public ResponseEntity<String> removeFriend(@PathVariable Long id) {
        try {
            Long currentUserId = authService.getCurrentUserId();
            pairService.removeFriend(currentUserId, id);
            return ResponseEntity.ok("Friend removed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error removing friend: " + e.getMessage());
        }
    }

}
