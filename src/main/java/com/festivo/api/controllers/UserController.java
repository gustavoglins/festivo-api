package com.festivo.api.controllers;

import com.festivo.api.request.user.UserUpdateRequestDTO;
import com.festivo.api.response.user.UserDetailsResponseDTO;
import com.festivo.domain.services.interfaces.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<UserDetailsResponseDTO> getUser(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Received request to get user details for: '{}'", userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUser(userDetails));
    }


    @PostMapping("/upload-profile-photo")
    public ResponseEntity<String> uploadProfilePhoto(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("file") MultipartFile file) throws IOException {
        userService.uploadProfilePicture(userDetails, file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/profile-photo")
    public ResponseEntity<byte[]> getProfilePhoto(@AuthenticationPrincipal UserDetails userDetails) {
        byte[] image = userService.getProfilePicture(userDetails);
        if (image == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(image);
    }

    @PutMapping
    public ResponseEntity<UserDetailsResponseDTO> update(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid UserUpdateRequestDTO userUpdateRequestDTO) {
        log.info("Received request to update user for user: {}", userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.OK).body(userService.update(userDetails, userUpdateRequestDTO));
    }
}