package com.festivo.api.request.user;

public record UserUpdateRequestDTO(

        String fullName,

        String email,

        String phoneNumber
) {
}
