package com.cabybara.aishortvideo.dto.auth;

import lombok.Data;

@Data
public class GoogleTokenResponseDTO {
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String scope;
    private String token_type;
    private String id_token;
}