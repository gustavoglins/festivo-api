package com.festivo.domain.services.impl;

import com.festivo.api.request.party.NewPartyRequestDTO;
import com.festivo.api.response.party.PartyDetailsResponseDTO;
import com.festivo.domain.entities.Party;
import com.festivo.domain.entities.User;
import com.festivo.domain.repositories.PartyRepository;
import com.festivo.domain.repositories.UserRepository;
import com.festivo.domain.services.interfaces.PartyService;
import com.festivo.shared.enums.PartyStatus;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Service
public class PartyServiceImpl implements PartyService {

    private final PartyRepository partyRepository;
    private final UserRepository userRepository;

    public PartyServiceImpl(PartyRepository partyRepository, UserRepository userRepository) {
        this.partyRepository = partyRepository;
        this.userRepository = userRepository;
    }

    private boolean isEventNameDefined(NewPartyRequestDTO newPartyRequestDTO) {
        return newPartyRequestDTO.name() != null && !newPartyRequestDTO.name().isEmpty();
    }

    private boolean isPartyBannerDefined(NewPartyRequestDTO newPartyRequestDTO) {
        return newPartyRequestDTO.banner() != null;
    }

    private byte[] loadDefaultPartyBanner() {
        try {
            return Files.readAllBytes(new ClassPathResource("default-banner.jpg").getFile().toPath());
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load default banner image");
        }
    }

    @Override
    public PartyDetailsResponseDTO create(UserDetails userDetails, NewPartyRequestDTO newPartyRequestDTO) {
        User partyCreator = userRepository.findByEmail(userDetails.getUsername());
        Party newParty = new Party();

        // Check if there is a name for the party, if not, set it to the party name: UserName + 's Party
        if (isEventNameDefined(newPartyRequestDTO)) newParty.setName(newPartyRequestDTO.name());
        else {
            String[] ownerFullNameParts = partyCreator.getFullName().split(" ");
            String eventName = ownerFullNameParts[0] + "'s Party";
            newParty.setName(eventName);
        }

        // Check if the user has set a banner, otherwise set a default banner
        if (isPartyBannerDefined(newPartyRequestDTO)) newParty.setBanner(newPartyRequestDTO.banner());
        else {
            newParty.setBanner(this.loadDefaultPartyBanner());
        }

        newParty.setDate(newPartyRequestDTO.date());
        newParty.setStartTime(newPartyRequestDTO.startTime());
        newParty.setEndTime(newPartyRequestDTO.endTime());
        newParty.setAddress(newPartyRequestDTO.address());
        newParty.setCreator(partyCreator);
        newParty.setOrganizers(new ArrayList<>());
        newParty.setGuests(new ArrayList<>());
        newParty.setStatus(PartyStatus.PUBLISHED);

        Party createdParty = partyRepository.save(newParty);

        return new PartyDetailsResponseDTO(
                createdParty.getId(),
                createdParty.getName(),
                createdParty.getDescription(),
                createdParty.getDate(),
                createdParty.getStartTime(),
                createdParty.getEndTime(),
                createdParty.getAddress(),
                createdParty.getBanner()
        );
    }

    @Override
    public PartyDetailsResponseDTO getPartyById(Long id) {
        Party party = partyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Party with ID: '" + id + "' not found"));

        return new PartyDetailsResponseDTO(
                party.getId(),
                party.getName(),
                party.getDescription(),
                party.getDate(),
                party.getStartTime(),
                party.getEndTime(),
                party.getAddress(),
                party.getBanner()
        );
    }

    @Override
    public List<PartyDetailsResponseDTO> getUserParties(UserDetails userDetails) {
        User user = userRepository.findByEmail(userDetails.getUsername());

        List<Party> parties = partyRepository.findAllByCreator(user);
        return parties.stream()
                .map(party -> new PartyDetailsResponseDTO(
                        party.getId(),
                        party.getName(),
                        party.getDescription(),
                        party.getDate(),
                        party.getStartTime(),
                        party.getEndTime(),
                        party.getAddress(),
                        party.getBanner()
                ))
                .toList();
    }
}
