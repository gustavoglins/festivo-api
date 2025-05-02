package com.festivo.api.request.auth;

import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequest(

        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Birth date is required")
        String birthDate
) {
}
