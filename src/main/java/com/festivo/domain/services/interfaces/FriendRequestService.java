package com.festivo.domain.services.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface FriendRequestService {

    void sendRequest(UserDetails userDetails, UUID receiverId);

    void acceptRequest(UUID requestId);

    void rejectRequest(UUID requestId);
}
