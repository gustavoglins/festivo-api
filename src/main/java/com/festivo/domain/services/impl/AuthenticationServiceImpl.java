package com.festivo.domain.services.impl;

import com.festivo.api.request.auth.ForgotPasswordRequest;
import com.festivo.api.request.auth.ResetPasswordRequest;
import com.festivo.api.request.user.UserLoginRequestDTO;
import com.festivo.api.request.user.UserSignupRequestDTO;
import com.festivo.api.response.user.AuthResponse;
import com.festivo.domain.dto.ResetPasswordEmailDTO;
import com.festivo.domain.entities.User;
import com.festivo.domain.repositories.UserRepository;
import com.festivo.domain.services.interfaces.AuthenticationService;
import com.festivo.domain.services.interfaces.EmailService;
import com.festivo.domain.services.interfaces.TokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Value("api.email")
    private String apiEmail;

    private static final int LEGAL_AGE = 18;
    private static final Logger log = LoggerFactory.getLogger(AuthenticationServiceImpl.class);

    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public AuthenticationServiceImpl(UserRepository userRepository, TokenService tokenService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    private boolean isOfLegalAge(LocalDate birthDate) {
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();
        return age >= LEGAL_AGE;
    }

    //TODO use this method
    private boolean isBirthDateValid(LocalDate birthDate) {
        return birthDate != null && !birthDate.isAfter(LocalDate.now());
    }

    private boolean isBirthDateMatching(User user, LocalDate birthDate) {
        return user.getBirthDate().equals(birthDate);
    }

    private boolean emailAlreadyExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public AuthResponse signup(UserSignupRequestDTO userSignupRequestDTO) {
        log.info("Signup attempt for user: '{}'", userSignupRequestDTO.email());
        if (isOfLegalAge(userSignupRequestDTO.birthDate())) {
            if (!emailAlreadyExists(userSignupRequestDTO.email())) {
                User newUser = new User(userSignupRequestDTO);
                String encryptedPassword = new BCryptPasswordEncoder().encode(userSignupRequestDTO.password());
                newUser.setPassword(encryptedPassword);

                userRepository.save(newUser);

                var token = tokenService.generateToken(newUser);
                log.info("Signup successful: User '{}' successfully registered.", userSignupRequestDTO.email());
                return new AuthResponse(token, newUser.getFullName());
            } else {
                log.error("User '{}' already registered.", userSignupRequestDTO.email());
                throw new RuntimeException("Signup failed: Email already registered");
            }
        } else {
            log.error("Signup failed: User must be at least 18 years old to register");
            throw new RuntimeException("User must be at least 18 years old to register"); //TODO create a custom exception for here
        }
    }

    @Override
    public AuthResponse login(UserLoginRequestDTO userLoginRequestDTO) {
        log.info("Login attempt for user: '{}'", userLoginRequestDTO.email());

        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(userLoginRequestDTO.email(), userLoginRequestDTO.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            var authenticatedUser = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
            log.info("Authentication successful for user: {}", authenticatedUser.getUsername());

            User user = userRepository.findByEmail(authenticatedUser.getUsername());
            if (user == null) {
                log.error("Authenticated user not found in the database: {}", authenticatedUser.getUsername());
                throw new IllegalStateException("User not found in the database");
            }

            var token = tokenService.generateToken(user);
            log.info("Login successful for user: {}", authenticatedUser.getUsername());

            return new AuthResponse(token, user.getFullName());
        } catch (Exception exception) {
            log.error("Error during login attempt for user: {}", exception.getMessage());
            throw new RuntimeException("Error during login attempt for user: ", exception);
        }
    }

    @Override
    public void sendResetPasswordEmail(ForgotPasswordRequest forgotPasswordRequest) throws Exception {
        User user = userRepository.findByEmail(forgotPasswordRequest.email());
        LocalDate birthDateEntered = LocalDate.parse(forgotPasswordRequest.birthDate());

        //TODO maybe implement logs in if case
        if (user == null || !isBirthDateMatching(user, birthDateEntered)) return;

        String token = tokenService.generateResetPasswordToken(user);

        ResetPasswordEmailDTO resetPasswordEmailDTO = new ResetPasswordEmailDTO(
                user.getEmail(),
                user.getFullName(),
                token
        );
        emailService.sendResetPasswordEmail(resetPasswordEmailDTO);
    }

    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        String email = tokenService.validateToken(resetPasswordRequest.token());

        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.error("User not found for email: {}", email);
            throw new RuntimeException("User not found");
        }

        user.setPassword(passwordEncoder.encode(resetPasswordRequest.newPassword()));
        userRepository.save(user);
    }
}
