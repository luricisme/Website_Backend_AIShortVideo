package com.cabybara.aishortvideo.dto.auth;

import com.cabybara.aishortvideo.dto.validator.EnumPattern;
import com.cabybara.aishortvideo.utils.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponseDTO {
    private String firstName;
    private String lastName;
    private String email;
    private UserRole role;
    private String username;
    private String bio;
    private String avatar;
    private String facebook;
    private String instagram;
    private String tiktok;
    private String youtube;
}
