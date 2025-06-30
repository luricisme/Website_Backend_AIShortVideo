package com.cabybara.aishortvideo.service.auth;

import com.cabybara.aishortvideo.dto.auth.GoogleTokenResponseDTO;
import com.cabybara.aishortvideo.dto.auth.GoogleUserInfoDTO;
import com.cabybara.aishortvideo.dto.auth.LoginResponseDTO;
import reactor.core.publisher.Mono;

import java.util.Optional;

public interface GoogleOauthService {
    Mono<GoogleTokenResponseDTO> exchangeAuthorizationCode(String code);
    GoogleUserInfoDTO getUserInfo(String accessToken);
    LoginResponseDTO authenticateWithGoogle(String code);
    Optional<GoogleTokenResponseDTO> refreshAccessToken(String refreshToken);
}
