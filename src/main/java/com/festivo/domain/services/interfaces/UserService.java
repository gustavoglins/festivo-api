package com.festivo.domain.services.interfaces;

import com.festivo.api.request.user.UserUpdateRequestDTO;
import com.festivo.api.response.user.UserDetailsResponseDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {

    UserDetailsResponseDTO getUser(UserDetails userDetails);
    void uploadProfilePicture(UserDetails userDetails, MultipartFile file);
    byte[] getProfilePicture(UserDetails userDetails);
    UserDetailsResponseDTO update(UserDetails userDetails, UserUpdateRequestDTO userUpdateRequestDTO);
}
