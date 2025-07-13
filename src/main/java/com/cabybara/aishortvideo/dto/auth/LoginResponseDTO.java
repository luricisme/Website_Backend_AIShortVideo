package com.cabybara.aishortvideo.dto.auth;

import com.cabybara.aishortvideo.utils.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginResponseDTO {
    private String jwt;
    private String username;
    private Long id;
    private String role;
    private UserStatus status;
}
