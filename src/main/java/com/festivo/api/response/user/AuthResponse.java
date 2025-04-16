package com.festivo.api.response.user;

public record AuthResponse(
        String token,
        String name
) {
}
