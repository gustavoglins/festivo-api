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
    public void sendRequest(UserDetails userDetails, UUID receiverId) {
        User sender = userRepository.findByEmail(userDetails.getUsername());

        if (sender.getId().equals(receiverId))
            throw new IllegalArgumentException("Sender and receiver cannot be the same.");

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found."));

        boolean alreadyExists = friendRequestRepository.findBySenderAndReceiver(sender, receiver).isPresent();
        if (alreadyExists) throw new IllegalArgumentException("Friend request already exists.");

        FriendRequest request = new FriendRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(request);
    }

    @Override
    public void acceptRequest(UUID requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found."));

        request.setStatus(FriendRequestStatus.ACCEPTED);

        User sender = request.getSender();
        User receiver = request.getReceiver();
        sender.getFriends().add(receiver);
        receiver.getFriends().add(sender);

        friendRequestRepository.save(request);
        userRepository.save(sender);
        userRepository.save(receiver);
    }

    @Override
    public void rejectRequest(UUID requestId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Friend request not found."));
        request.setStatus(FriendRequestStatus.REJECTED);
        friendRequestRepository.save(request);
    }
}
