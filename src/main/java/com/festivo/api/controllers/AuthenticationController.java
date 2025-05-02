package com.festivo.api.controllers;

import com.festivo.api.request.auth.ForgotPasswordRequest;
import com.festivo.api.request.auth.ResetPasswordRequest;
import com.festivo.api.request.user.UserLoginRequestDTO;
import com.festivo.api.request.user.UserSignupRequestDTO;
import com.festivo.api.response.user.AuthResponse;
import com.festivo.domain.services.interfaces.AuthenticationService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody @Valid UserSignupRequestDTO userSignupRequestDTO) {
        log.info("Received user signup request for: '{}'.", userSignupRequestDTO.email());
        AuthResponse response = authenticationService.signup(userSignupRequestDTO);
        log.info("Successfully signed up user with email: {}", userSignupRequestDTO.email());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid UserLoginRequestDTO userLoginRequestDTO) {
        log.info("Received user login request for: '{}'.", userLoginRequestDTO.email());
        AuthResponse response = authenticationService.login(userLoginRequestDTO);
        log.info("Successfully logged in user with email: {}", userLoginRequestDTO.email());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest forgotPasswordRequest) throws Exception {
        log.info("Received forgot password request for user: '{}'", forgotPasswordRequest.email());
        authenticationService.sendResetPasswordEmail(forgotPasswordRequest);
        log.info("Successfully, if the email exists, the password reset email has been sent to the user: '{}'", forgotPasswordRequest.email());
        return ResponseEntity.ok(Map.of("message", "If the email exists, a reset password link has been sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest) {
        log.info("Received reset password request for user: '{}'", resetPasswordRequest.token());
        authenticationService.resetPassword(resetPasswordRequest);
        log.info("Successfully reset password for user: '{}'", resetPasswordRequest.token());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
