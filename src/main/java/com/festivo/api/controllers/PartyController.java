package com.festivo.api.controllers;

import com.festivo.api.request.party.NewPartyRequestDTO;
import com.festivo.api.response.party.PartyDetailsResponseDTO;
import com.festivo.domain.services.interfaces.PartyService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/party")
public class PartyController {

    private static final Logger log = LoggerFactory.getLogger(PartyController.class);

    private final PartyService partyService;

    public PartyController(PartyService partyService) {
        this.partyService = partyService;
    }

    @PostMapping
    public ResponseEntity<PartyDetailsResponseDTO> createParty(@AuthenticationPrincipal UserDetails userDetails, @RequestBody @Valid NewPartyRequestDTO newPartyRequestDTO) {
        log.info("Received request to create a new party, created by '{}'", userDetails.getUsername());
        PartyDetailsResponseDTO response = partyService.create(userDetails, newPartyRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PartyDetailsResponseDTO> getPartyById(@PathVariable("id") Long id) {
        log.info("Received request to get a party by id: '{}'", id);
        PartyDetailsResponseDTO response = partyService.getPartyById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PartyDetailsResponseDTO>> getUserParties(@AuthenticationPrincipal UserDetails userDetails) {
        log.info("Received request to get user parties for user '{}'", userDetails.getUsername());
        List<PartyDetailsResponseDTO> response = partyService.getUserParties(userDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
