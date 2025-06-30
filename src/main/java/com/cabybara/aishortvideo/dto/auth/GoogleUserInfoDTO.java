package com.cabybara.aishortvideo.dto.auth;

import lombok.Data;

@Data
public class GoogleUserInfoDTO {
    private String sub;
    private String email;
    private String name;
    private String given_name;
    private String family_name;
    private String picture;
    private boolean email_verified;
}
