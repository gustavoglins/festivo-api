package com.festivo.domain.services.interfaces;

import com.festivo.api.request.user.UserLoginRequestDTO;
import com.festivo.api.request.user.UserSignupRequestDTO;
import com.festivo.api.response.user.AuthResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface AuthenticationService {

    AuthResponse signup(UserSignupRequestDTO userSignupRequestDTO);
    AuthResponse login(UserLoginRequestDTO userLoginRequestDTO);
}
