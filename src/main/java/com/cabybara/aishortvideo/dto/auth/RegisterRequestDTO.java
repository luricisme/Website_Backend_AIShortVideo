package com.cabybara.aishortvideo.dto.auth;

import com.cabybara.aishortvideo.dto.validator.EnumPattern;
import com.cabybara.aishortvideo.utils.UserRole;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {
    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    private String email;

    @NotNull(message = "Role cannot be null")
    @Schema(type = "string", allowableValues = {"USER", "ADMIN"}, example = "USER")
    private UserRole role;

    @NotBlank
    private String username;

    @NotBlank
    private String password;
    private String bio;
    private String avatar;
    private String facebook;
    private String instagram;
    private String tiktok;
    private String youtube;
}
