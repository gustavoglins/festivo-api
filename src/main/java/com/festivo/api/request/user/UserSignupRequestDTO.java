package com.festivo.api.request.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UserSignupRequestDTO(

        @NotBlank(message = "Full name is required.")
        String fullName,

        @NotBlank(message = "Email is required.")
        String email,

        @NotBlank(message = "Password is required.")
        String password,

        String phoneNumber,

        String profilePicture,

        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "Birth date is required.")
        LocalDate birthDate
) {
}
