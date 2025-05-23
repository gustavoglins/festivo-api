package com.festivo.domain.services.interfaces;

import com.festivo.api.request.party.NewPartyRequestDTO;
import com.festivo.api.response.party.PartyDetailsResponseDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.List;

public interface PartyService {

    PartyDetailsResponseDTO create(UserDetails userDetails, NewPartyRequestDTO newPartyRequestDTO) throws IOException;

    PartyDetailsResponseDTO getPartyById(Long id);

    List<PartyDetailsResponseDTO> getUserParties(UserDetails userDetails);
}
