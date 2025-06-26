package com.cabybara.aishortvideo.service.user;

import com.cabybara.aishortvideo.dto.auth.RegisterRequestDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterResponseDTO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService  {
    RegisterResponseDTO addUser(RegisterRequestDTO registerRequestDTO);

    
}
