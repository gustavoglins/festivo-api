package com.festivo.domain.services.interfaces;

import com.festivo.api.request.user.UserLoginRequestDTO;
import com.festivo.api.request.user.UserSignupRequestDTO;
import com.festivo.api.response.user.AuthResponse;

public interface AuthenticationService {

    AuthResponse signup(UserSignupRequestDTO userSignupRequestDTO);
    AuthResponse login(UserLoginRequestDTO userLoginRequestDTO);
}
