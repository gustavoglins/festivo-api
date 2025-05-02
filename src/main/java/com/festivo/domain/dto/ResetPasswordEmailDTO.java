package com.festivo.domain.dto;

public record ResetPasswordEmailDTO(

        String to,
        String username,
        String token
) {
}
