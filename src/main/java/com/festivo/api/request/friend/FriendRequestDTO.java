package com.festivo.api.request.friend;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record FriendRequestDTO(

        @NotBlank(message = "Receiver id is required.")
        UUID userId
) {
}
