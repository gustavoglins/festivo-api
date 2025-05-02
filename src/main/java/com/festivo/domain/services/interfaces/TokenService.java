package com.festivo.domain.services.interfaces;

import com.festivo.domain.entities.User;

public interface TokenService {

    String generateToken(User user);
    String generateResetPasswordToken(User user);
    String validateToken(String token);
}
