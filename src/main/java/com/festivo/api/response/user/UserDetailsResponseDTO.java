package com.festivo.api.response.user;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.UUID;

@JsonPropertyOrder({"fullName", "email", "phoneNumber"})
public record UserDetailsResponseDTO(

        UUID id,

        String fullName,

        String email,

        String phoneNumber
) {
}
