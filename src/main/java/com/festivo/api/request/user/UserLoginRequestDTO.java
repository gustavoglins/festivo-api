package com.festivo.api.request.user;

import jakarta.validation.constraints.NotBlank;

public record UserLoginRequestDTO(

        @NotBlank(message = "Email is required.")
        String email,

        @NotBlank(message = "Password is required.")
        String password
) {
}
