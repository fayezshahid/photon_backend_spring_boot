package com.photon.services;

import com.photon.entities.Pair;
import com.photon.entities.User;
import com.photon.repositories.PairRepository;
import com.photon.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PairService {

    private final PairRepository pairRepository;
    private final UserRepository userRepository;

    /**
     * Get all users excluding those who are already friends, have pending requests, or are the current user
     */
    public List<User> getAvailableUsers(Long currentUserId) {
        // Get users where current user sent accepted requests
        List<Long> sentAcceptedIds = pairRepository.findBySenderIdAndIsAccepted(currentUserId, true)
                .stream()
                .map(pair -> pair.getReceiver().getId())
                .toList();

        // Get users where current user received accepted requests
        List<Long> receivedAcceptedIds = pairRepository.findByReceiverIdAndIsAccepted(currentUserId, true)
                .stream()
                .map(pair -> pair.getSender().getId())
                .toList();

        // Get users where current user received pending requests
        List<Long> receivedPendingIds = pairRepository.findByReceiverIdAndIsAccepted(currentUserId, false)
                .stream()
                .map(pair -> pair.getSender().getId())
                .toList();

        // Combine all excluded IDs
        List<Long> excludedIds = new ArrayList<>();
        excludedIds.addAll(sentAcceptedIds);
        excludedIds.addAll(receivedAcceptedIds);
        excludedIds.addAll(receivedPendingIds);
        excludedIds.add(currentUserId); // Exclude current user

        // If no exclusions, return all users except current user
        if (excludedIds.size() == 1) { // Only current user in exclusions
            return userRepository.findAll().stream()
                    .filter(user -> !user.getId().equals(currentUserId))
                    .collect(Collectors.toList());
        }

        return userRepository.findUsersNotInList(excludedIds);
    }

    /**
     * Get all friends (users with accepted pair relationships)
     */
    public List<User> getFriends(Long currentUserId) {
        // Get users where current user sent accepted requests
        List<Long> sentAcceptedIds = pairRepository.findBySenderIdAndIsAccepted(currentUserId, true)
                .stream()
                .map(pair -> pair.getReceiver().getId())
                .toList();

        // Get users where current user received accepted requests
        List<Long> receivedAcceptedIds = pairRepository.findByReceiverIdAndIsAccepted(currentUserId, true)
                .stream()
                .map(pair -> pair.getSender().getId())
                .toList();

        // Combine friend IDs
        List<Long> friendIds = new ArrayList<>();
        friendIds.addAll(sentAcceptedIds);
        friendIds.addAll(receivedAcceptedIds);

        if (friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        return userRepository.findUsersByIds(friendIds);
    }

    /**
     * Get all pending friend requests received by current user
     */
    public List<User> getPendingRequests(Long currentUserId) {
        List<Long> requestSenderIds = pairRepository.findByReceiverIdAndIsAccepted(currentUserId, false)
                .stream()
                .map(pair -> pair.getSender().getId())
                .collect(Collectors.toList());

        if (requestSenderIds.isEmpty()) {
            return new ArrayList<>();
        }

        return userRepository.findUsersByIds(requestSenderIds);
    }

    /**
     * Get IDs of users to whom current user has sent pending requests
     */
    public List<Long> getSentRequestIds(Long currentUserId) {
        return pairRepository.findBySenderIdAndIsAccepted(currentUserId, false)
                .stream()
                .map(pair -> pair.getReceiver().getId())
                .collect(Collectors.toList());
    }

    /**
     * Send a friend request
     */
    public void sendRequest(Long senderId, Long receiverId) {
        // Check if pair already exists
        if (pairRepository.findBySenderIdAndReceiverId(senderId, receiverId).isPresent()) {
            throw new IllegalStateException("Friend request already exists");
        }

        // Check if reverse pair exists
        if (pairRepository.findBySenderIdAndReceiverId(receiverId, senderId).isPresent()) {
            throw new IllegalStateException("Friend request already exists in reverse");
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Pair pair = Pair.builder()
                .sender(sender)
                .receiver(receiver)
                .isAccepted(false)
                .build();

        pairRepository.save(pair);
    }

    /**
     * Delete a sent friend request
     */
    public void deleteRequest(Long senderId, Long receiverId) {
        pairRepository.deleteBySenderIdAndReceiverId(senderId, receiverId);
    }

    /**
     * Accept a friend request
     */
    public void acceptRequest(Long currentUserId, Long senderId) {
        pairRepository.updateAcceptanceStatus(currentUserId, senderId, true);
    }

    /**
     * Reject a friend request
     */
    public void rejectRequest(Long currentUserId, Long senderId) {
        pairRepository.deleteBySenderIdAndReceiverId(senderId, currentUserId);
    }

    /**
     * Remove a friend (delete pair relationship)
     */
    public void removeFriend(Long currentUserId, Long friendId) {
        pairRepository.deletePairBetweenUsers(currentUserId, friendId);
    }
}