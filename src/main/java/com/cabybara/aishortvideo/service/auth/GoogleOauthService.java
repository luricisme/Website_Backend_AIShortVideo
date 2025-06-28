package com.cabybara.aishortvideo.service.auth;

import com.cabybara.aishortvideo.dto.auth.GoogleTokenResponseDTO;
import reactor.core.publisher.Mono;

public interface GoogleOauthService {
    Mono<GoogleTokenResponseDTO> exchangeAuthorizationCode(String code);
}
