package com.festivo.domain.services.impl;

import com.festivo.domain.entities.FriendRequest;
import com.festivo.domain.entities.User;
import com.festivo.domain.repositories.FriendRequestRepository;
import com.festivo.domain.repositories.UserRepository;
import com.festivo.domain.services.interfaces.FriendRequestService;
import com.festivo.shared.enums.FriendRequestStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @Override
    public void sendRequest(UserDetails userDetails, UUID friendId) {
        User sender = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Sender not found."));

        User receiver = userRepository.findById(friendId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found."));

        if (sender.getId().equals(receiver.getId()))
            throw new IllegalArgumentException("Sender and receiver cannot be the same.");
        boolean alreadyExists = friendRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent();

        if (alreadyExists) throw new IllegalArgumentException("Friend request already exists.");
        else {
            FriendRequest friendRequest = new FriendRequest();
            friendRequest.setSender(sender);
            friendRequest.setReceiver(receiver);
            friendRequest.setStatus(FriendRequestStatus.PENDING);
            friendRequestRepository.save(friendRequest);
        }
    }

    @Override
    public void acceptRequest(UserDetails userDetails, UUID friendId) {
        FriendRequest friendRequest = friendRequestRepository.findBySenderAndReceiver(
                userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new IllegalArgumentException("Sender not found.")),
                userRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Receiver not found."))
        ).orElseThrow(() -> new IllegalArgumentException("Friend request not found."));

        friendRequest.setStatus(FriendRequestStatus.ACCEPTED);

        User sender = friendRequest.getSender();
        User receiver = friendRequest.getReceiver();

        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        friendRequestRepository.save(friendRequest);
        userRepository.save(sender);
        userRepository.save(receiver);
    }

    @Override
    public void rejectRequest(UserDetails userDetails, UUID friendId) {
        FriendRequest friendRequest = friendRequestRepository.findBySenderAndReceiver(
                userRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new IllegalArgumentException("Sender not found.")),
                userRepository.findById(friendId).orElseThrow(() -> new IllegalArgumentException("Receiver not found."))
        ).orElseThrow(() -> new IllegalArgumentException("Friend request not found."));

        friendRequest.setStatus(FriendRequestStatus.REJECTED);
        friendRequestRepository.save(friendRequest);
    }
}
