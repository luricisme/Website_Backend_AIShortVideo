package com.cabybara.aishortvideo.service.user;

import com.cabybara.aishortvideo.dto.auth.GoogleTokenResponseDTO;
import com.cabybara.aishortvideo.dto.auth.GoogleUserInfoDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterRequestDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterResponseDTO;
import com.cabybara.aishortvideo.dto.user.UpdateUserDTO;
import com.cabybara.aishortvideo.dto.user.UserDTO;
import com.cabybara.aishortvideo.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService extends UserDetailsService  {
    RegisterResponseDTO addUser(RegisterRequestDTO registerRequestDTO);

    UserDTO loadUserById(Long id);

    UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO);

    void deleteUser(Long id);

    User findOrCreateGoogleUser(GoogleUserInfoDTO userInfo, GoogleTokenResponseDTO tokenResponse);

    String updateAvatar(MultipartFile avatar, Long userId) throws IOException;
}
