package com.cabybara.aishortvideo.service.interfaces;

import com.cabybara.aishortvideo.dto.auth.RegisterRequestDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

public interface UserServiceInterface extends UserDetailsService  {
    RegisterResponseDTO addUser(RegisterRequestDTO registerRequestDTO);
}
