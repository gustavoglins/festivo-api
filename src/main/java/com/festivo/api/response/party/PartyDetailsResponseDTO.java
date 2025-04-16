package com.festivo.api.response.party;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.festivo.domain.entities.Address;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonPropertyOrder({"name", "description"})
public record PartyDetailsResponseDTO(

        Long id,
        String name,
        String description,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        Address address,
        byte[] banner
) {
}
