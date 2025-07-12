package com.cabybara.aishortvideo.dto.user;

import com.cabybara.aishortvideo.utils.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class UpdateUserDTO {
    private String firstName;

    private String lastName;

    @Email
    private String email;

    private String username;

    private String bio;
    private String avatar;
    private String facebook;
    private String instagram;
    private String tiktok;
    private String youtube;
}
