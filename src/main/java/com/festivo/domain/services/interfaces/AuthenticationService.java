package com.festivo.domain.services.interfaces;

import com.festivo.api.request.auth.ForgotPasswordRequest;
import com.festivo.api.request.auth.ResetPasswordRequest;
import com.festivo.api.request.user.UserLoginRequestDTO;
import com.festivo.api.request.user.UserSignupRequestDTO;
import com.festivo.api.response.user.AuthResponse;
import jakarta.mail.MessagingException;

public interface AuthenticationService {

    AuthResponse signup(UserSignupRequestDTO userSignupRequestDTO);
    AuthResponse login(UserLoginRequestDTO userLoginRequestDTO);
    void sendResetPasswordEmail(ForgotPasswordRequest forgotPasswordRequest) throws Exception;
    void resetPassword(ResetPasswordRequest resetPasswordRequest);
}
