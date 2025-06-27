package com.cabybara.aishortvideo.dto.user;

import com.cabybara.aishortvideo.utils.UserRole;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class UpdateUserDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String bio;
    private String facebook;
    private String instagram;
    private String tiktok;
    private String youtube;
}
