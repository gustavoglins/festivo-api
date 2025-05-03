package com.festivo.domain.services.impl;

import com.festivo.api.request.party.NewPartyRequestDTO;
import com.festivo.api.response.party.PartyDetailsResponseDTO;
import com.festivo.domain.entities.Party;
import com.festivo.domain.entities.User;
import com.festivo.domain.repositories.PartyRepository;
import com.festivo.domain.repositories.UserRepository;
import com.festivo.domain.services.interfaces.AwsS3Service;
import com.festivo.domain.services.interfaces.PartyService;
import com.festivo.shared.enums.PartyStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Slf4j
@Service
public class PartyServiceImpl implements PartyService {

    private final PartyRepository partyRepository;
    private final UserRepository userRepository;
    private final AwsS3Service awsS3Service;

    public PartyServiceImpl(PartyRepository partyRepository, UserRepository userRepository, AwsS3Service awsS3Service) {
        this.partyRepository = partyRepository;
        this.userRepository = userRepository;
        this.awsS3Service = awsS3Service;
    }

    private boolean isEventNameDefined(NewPartyRequestDTO newPartyRequestDTO) {
        return newPartyRequestDTO.name() != null && !newPartyRequestDTO.name().isEmpty();
    }

    private byte[] loadDefaultPartyBanner() {
        try {
            return Files.readAllBytes(new ClassPathResource("default-banner.jpg").getFile().toPath());
        } catch (Exception exception) {
            throw new RuntimeException("Failed to load default banner image");
        }
    }

    @Override
    public PartyDetailsResponseDTO create(UserDetails userDetails, NewPartyRequestDTO newPartyRequestDTO) throws IOException {
        log.info(newPartyRequestDTO.toString());
        User partyCreator = userRepository.findByEmail(userDetails.getUsername());
        Party newParty = new Party();

        // Checks if there is a name for the party, if not, set it to the party name: "Username's Party"
        if (isEventNameDefined(newPartyRequestDTO)) newParty.setName(newPartyRequestDTO.name());
        else {
            String[] ownerFullNameParts = partyCreator.getFullName().split(" ");
            String eventName = ownerFullNameParts[0] + "'s Party";
            newParty.setName(eventName);
        }

        MultipartFile multipartFile = convertBase64ToMultipartFile(
                newPartyRequestDTO.banner(),
                "party-banner.jpg",
                "image/jpeg"
        );

        newParty.setDate(newPartyRequestDTO.date());
        newParty.setStartTime(newPartyRequestDTO.startTime());
        newParty.setEndTime(newPartyRequestDTO.endTime());
        newParty.setAddress(newPartyRequestDTO.address());
        newParty.setCreator(partyCreator);
        newParty.setOrganizers(new ArrayList<>());
        newParty.setGuests(new ArrayList<>());
        newParty.setStatus(PartyStatus.PUBLISHED);

        String bannerUrl = awsS3Service.uploadFile(multipartFile);
        newParty.setBannerUrl(bannerUrl);

        Party createdParty = partyRepository.save(newParty);

        return new PartyDetailsResponseDTO(
                createdParty.getId(),
                createdParty.getName(),
                createdParty.getDescription(),
                createdParty.getDate(),
                createdParty.getStartTime(),
                createdParty.getEndTime(),
                createdParty.getAddress(),
                createdParty.getBannerUrl()
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
                party.getBannerUrl()
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
                        party.getBannerUrl()
                ))
                .toList();
    }


    private MultipartFile convertBase64ToMultipartFile(String base64String, String fileName, String contentType) throws IOException {
        String[] parts = base64String.split(",");
        String base64File = parts.length > 1 ? parts[1] : parts[0];
        byte[] decodedBytes = Base64.getDecoder().decode(base64File);
        return new MultipartFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public String getOriginalFilename() {
                return fileName;
            }

            @Override
            public String getContentType() {
                return contentType;
            }

            @Override
            public boolean isEmpty() {
                return decodedBytes.length == 0;
            }

            @Override
            public long getSize() {
                return decodedBytes.length;
            }

            @Override
            public byte[] getBytes() throws IOException {
                return decodedBytes;
            }

            @Override
            public InputStream getInputStream() throws IOException {
                return new ByteArrayInputStream(decodedBytes);
            }

            @Override
            public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
                java.nio.file.Files.copy(getInputStream(), dest.toPath());
            }
        };
    }
}
