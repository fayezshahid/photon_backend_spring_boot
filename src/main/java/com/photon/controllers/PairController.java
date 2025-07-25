package com.photon.controllers;

import com.photon.dtos.UserDTO;
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
import java.util.Map;

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
    public ResponseEntity<List<UserDTO>> getUsers() {
        Long currentUserId = authService.getCurrentUserId();
        List<User> users = pairService.getAvailableUsers(currentUserId);

        List<UserDTO> userDtos = users.stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail()))
                .toList();

        return ResponseEntity.ok(userDtos);
    }

    /**
     * Get friends
     */
    @GetMapping("/friends")
    public ResponseEntity<List<UserDTO>> getFriends() {
        Long currentUserId = authService.getCurrentUserId();
        List<User> friends = pairService.getFriends(currentUserId);

        List<UserDTO> friendsDtos = friends.stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail()))
                .toList();

        return ResponseEntity.ok(friendsDtos);
    }

    /**
     * Get pending friend requests
     */
    @GetMapping("/requests")
    public ResponseEntity<List<UserDTO>> getRequests() {
        Long currentUserId = authService.getCurrentUserId();
        List<User> requests = pairService.getPendingRequests(currentUserId);

        List<UserDTO> requestDtos = requests.stream()
                .map(user -> new UserDTO(user.getId(), user.getEmail()))
                .toList();

        return ResponseEntity.ok(requestDtos);
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
    public ResponseEntity<Map<String, String>> sendRequest(@PathVariable Long id) {
        try {
            Long currentUserId = authService.getCurrentUserId();
            pairService.sendRequest(currentUserId, id);
            return ResponseEntity.ok(Map.of("message", "Request sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error sending friend request: " + e.getMessage()));
        }
    }

    /**
     * Delete sent friend request
     */
    @DeleteMapping("/request/{id}")
    public ResponseEntity<Map<String, String>> deleteRequest(@PathVariable Long id) {
        try {
            Long currentUserId = authService.getCurrentUserId();
            pairService.deleteRequest(currentUserId, id);
            return ResponseEntity.ok(Map.of("message", "Request deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error deleting friend request: " + e.getMessage()));
        }
    }

    /**
     * Accept friend request
     */
    @PutMapping("/request/{id}/accept")
    public ResponseEntity<Map<String, String>> acceptRequest(@PathVariable Long id) {
        try {
            Long currentUserId = authService.getCurrentUserId();
            pairService.acceptRequest(currentUserId, id);
            return ResponseEntity.ok(Map.of("message", "Request accepted successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error accepting friend request: " + e.getMessage()));
        }
    }

    /**
     * Reject friend request
     */
    @DeleteMapping("/request/{id}/reject")
    public ResponseEntity<Map<String, String>> rejectRequest(@PathVariable Long id) {
        try {
            Long currentUserId = authService.getCurrentUserId();
            pairService.rejectRequest(currentUserId, id);
            return ResponseEntity.ok(Map.of("message", "Request rejected successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error rejecting friend request: " + e.getMessage()));
        }
    }

    /**
     * Remove friend
     */
    @DeleteMapping("/friend/{id}")
    public ResponseEntity<Map<String, String>> removeFriend(@PathVariable Long id) {
        try {
            Long currentUserId = authService.getCurrentUserId();
            pairService.removeFriend(currentUserId, id);
            return ResponseEntity.ok(Map.of("message", "Friend removed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error removing friend request: " + e.getMessage()));
        }
    }

}
