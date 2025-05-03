package com.festivo.api.request.party;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.festivo.domain.entities.Address;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record NewPartyRequestDTO(

        String name,

        String description,

        @JsonFormat(pattern = "yyyy-MM-dd")
        @NotNull(message = "Date is required.")
        LocalDate date,

        @NotNull(message = "Start time is required.")
        LocalTime startTime,

        @NotNull(message = "End time is required.")
        LocalTime endTime,

        @NotNull(message = "Address is required.")
        Address address,

        String banner) {
}
