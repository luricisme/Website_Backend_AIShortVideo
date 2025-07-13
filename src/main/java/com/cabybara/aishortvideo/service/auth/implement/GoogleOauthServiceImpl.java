package com.cabybara.aishortvideo.service.auth.implement;

import com.cabybara.aishortvideo.dto.auth.GoogleTokenResponseDTO;
import com.cabybara.aishortvideo.dto.auth.GoogleUserInfoDTO;
import com.cabybara.aishortvideo.dto.auth.LoginResponseDTO;
import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.service.auth.GoogleOauthService;
import com.cabybara.aishortvideo.service.user.implement.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
@Slf4j
public class GoogleOauthServiceImpl implements GoogleOauthService {
    private final WebClient googleOAuth2TokenWebClient;
    private final WebClient googleWebClient;
    private final JwtServiceImpl jwtService;
    private final UserServiceImpl userService;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    public GoogleOauthServiceImpl(
            @Qualifier("googleOAuth2TokenWebClient") WebClient googleOAuth2TokenWebClient,
            @Qualifier("googleWebClient") WebClient googleWebClient,
            JwtServiceImpl jwtService,
            UserServiceImpl userService
    ) {
        this.googleOAuth2TokenWebClient = googleOAuth2TokenWebClient;
        this.googleWebClient = googleWebClient;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public Mono<GoogleTokenResponseDTO> exchangeAuthorizationCode(String code) {
        return googleOAuth2TokenWebClient.post()
                .uri("/token")
                .body(BodyInserters.fromFormData("code", code)
                        .with("client_id", clientId)
                        .with("client_secret", clientSecret)
                        .with("redirect_uri", redirectUri)
                        .with("grant_type", "authorization_code"))
                .retrieve()
                .bodyToMono(GoogleTokenResponseDTO.class);
    }

    @Override
    public Optional<GoogleTokenResponseDTO> refreshAccessToken(String refreshToken) {
        try {
            return Optional.ofNullable(
                    googleOAuth2TokenWebClient.post()
                            .uri("/token")
                            .body(BodyInserters
                                    .fromFormData("client_id", clientId)
                                    .with("client_secret", clientSecret)
                                    .with("refresh_token", refreshToken)
                                    .with("grant_type", "refresh_token"))
                            .retrieve()
                            .bodyToMono(GoogleTokenResponseDTO.class)
                            .block()
            );
        } catch (WebClientResponseException ex) {
            log.error("Failed to refresh Google token: {}", ex.getResponseBodyAsString());
            return Optional.empty();
        }
    }

    @Override
    public GoogleUserInfoDTO getUserInfo(String accessToken) {
        return googleWebClient.get()
                .uri("/oauth2/v3/userinfo")
                .headers(headers -> headers.setBearerAuth(accessToken))
                .retrieve()
                .bodyToMono(GoogleUserInfoDTO.class)
                .block();
    }

    @Override
    public LoginResponseDTO authenticateWithGoogle(String code) {
        // 1. Exchange token
        GoogleTokenResponseDTO tokenResponse = exchangeAuthorizationCode(code).block();

        if (tokenResponse == null || tokenResponse.getAccess_token() == null) {
            throw new RuntimeException("Error when getting access token");
        }

        // 2. Get user info
        GoogleUserInfoDTO userInfo = getUserInfo(tokenResponse.getAccess_token());

        if (userInfo == null || userInfo.getEmail() == null) {
            throw new RuntimeException("Cannot get user info from google");
        }

        // 3. Save or update user & token
        User user = userService.findOrCreateGoogleUser(userInfo, tokenResponse);

        // 4. Generate jwt
        String jwt = jwtService.generateToken(user.getEmail());

        return LoginResponseDTO.builder()
                .id(user.getId())
                .jwt(jwt)
                .role(user.getRole().name())
                .username(user.getUsername())
                .build();
    }
}
