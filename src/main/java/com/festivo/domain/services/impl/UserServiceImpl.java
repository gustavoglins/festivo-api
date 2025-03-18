package com.festivo.domain.services.impl;

import com.festivo.api.request.user.UserUpdateRequestDTO;
import com.festivo.api.response.user.UserDetailsResponseDTO;
import com.festivo.domain.entities.User;
import com.festivo.domain.repositories.UserRepository;
import com.festivo.domain.services.interfaces.TokenService;
import com.festivo.domain.services.interfaces.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final TokenService tokenService;

    public UserServiceImpl(UserRepository userRepository, TokenService tokenService) {
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    private Boolean emailHasBeenChanged(UserDetails userDetails, String email) {
        if (userDetails.getUsername().equals(email)) {
            return false;
        } else {
            return !emailAlreadyInUse(email);
        }
    }

    private Boolean phoneNumberHasBeenChanged(User user, String phoneNumber) {
        if (user.getPhoneNumber().equals(phoneNumber)) {
            return false;
        } else {
            return !phoneNumberAlreadyInUse(phoneNumber);
        }
    }

    private Boolean emailAlreadyInUse(String email) {
        return userRepository.existsByEmail(email);
    }

    private Boolean phoneNumberAlreadyInUse(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public UserDetailsResponseDTO getUser(UserDetails userDetails) {
        log.info("Attempting to get user '{}'.", userDetails.getUsername());
        String email = userDetails.getUsername();
        User user = userRepository.findByEmail(email);
        if (user == null) throw new RuntimeException("User not found");
        else return new UserDetailsResponseDTO(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhoneNumber()
        );
    }

    @Override
    public void uploadProfilePicture(UserDetails userDetails, MultipartFile file) {
        User user = userRepository.findByEmail(userDetails.getUsername());

        try {
            byte[] imageBytes = file.getBytes();
            user.setProfilePicture(imageBytes);
            userRepository.save(user);
        } catch (IOException e) {
            throw new RuntimeException("Error uploading profile picture", e);
        }
    }

    @Override
    public byte[] getProfilePicture(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername());
        return user.getProfilePicture();
    }

    @Override
    public UserDetailsResponseDTO update(UserDetails userDetails, UserUpdateRequestDTO userUpdateRequestDTO) {
        log.info("Attempting to update user '{}'.", userDetails.getUsername());
        String email = userDetails.getUsername();
        User retrievedUser = userRepository.findByEmail(email);

        if (emailHasBeenChanged(userDetails, email)) {
            if (emailAlreadyInUse(userUpdateRequestDTO.email())) {
                throw new RuntimeException("Email already in use."); //TODO
            } else {
                String newToken = tokenService.generateToken(retrievedUser);
            }
        }
        if (phoneNumberHasBeenChanged(retrievedUser, userUpdateRequestDTO.phoneNumber())) {
            if (phoneNumberAlreadyInUse(userUpdateRequestDTO.phoneNumber())) {
                throw new RuntimeException("Phone number already in use."); //TODO
            }
        }

        retrievedUser.setFullName(userUpdateRequestDTO.fullName());
        retrievedUser.setPhoneNumber(userUpdateRequestDTO.phoneNumber());

        userRepository.save(retrievedUser);
        return new UserDetailsResponseDTO(
                retrievedUser.getId(),
                retrievedUser.getFullName(),
                retrievedUser.getEmail(),
                retrievedUser.getPhoneNumber()
        );
    }
}
