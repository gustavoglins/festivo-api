package com.festivo.api.request.auth;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(

        @NotBlank(message = "Token is required")
        String token,

        @NotBlank(message = "New password is required")
        String newPassword
) {
}
