package com.festivo.domain.services.interfaces;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface FriendRequestService {

    void sendRequest(UserDetails userDetails, UUID friendId);
    void acceptRequest(UserDetails userDetails, UUID friendId);
    void rejectRequest(UserDetails userDetails, UUID friendId);
}
