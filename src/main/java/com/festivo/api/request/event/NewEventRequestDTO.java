package com.festivo.api.request.event;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record NewEventRequestDTO(

        @NotBlank(message = "Name is required.")
        String name,

        String description,

        @NotNull(message = "Start date is required.")
        LocalDateTime startDate,

        @NotNull(message = "End date is required.")
        LocalDateTime endDate,  

        @NotNull(message = "Location is required.")
        String location,

        Double latitude,

        String eventLogo,

        String eventBanner) {
}
