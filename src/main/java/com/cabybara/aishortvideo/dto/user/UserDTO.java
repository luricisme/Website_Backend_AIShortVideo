package com.cabybara.aishortvideo.dto.user;

import com.cabybara.aishortvideo.utils.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
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
    private Set<UserFollowerDTO> followers;
    private Set<UserFollowerDTO> followings;
}
