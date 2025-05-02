package com.festivo.domain.services.interfaces;

import com.festivo.domain.dto.ResetPasswordEmailDTO;

public interface EmailService {
    void sendResetPasswordEmail(ResetPasswordEmailDTO resetPasswordEmailDTO);
}
