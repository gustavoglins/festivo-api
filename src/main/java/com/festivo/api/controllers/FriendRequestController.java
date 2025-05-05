package com.festivo.api.controllers;

import com.festivo.api.request.friend.FriendRequestDTO;
import com.festivo.domain.services.interfaces.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/friend-request")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;

//    @PostMapping("/send")
//    public ResponseEntity<Void> sendFriendRequest(@AuthenticationPrincipal UserDetails userDetails, @RequestBody FriendRequestDTO friendRequestDTO) {
//        friendRequestService.sendRequest(userDetails, friendRequestDTO.receiverId());
//        return ResponseEntity.noContent().build();
//    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendFriendRequest(@AuthenticationPrincipal UserDetails userDetails, @RequestBody FriendRequestDTO friendRequestDTO) {
        friendRequestService.sendRequest(userDetails, friendRequestDTO.receiverId());
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<Void> acceptFriendRequest(@AuthenticationPrincipal UserDetails userDetails, @PathVariable UUID id) {
        friendRequestService.acceptRequest(id);
        return ResponseEntity.noContent().build();
    }
}
