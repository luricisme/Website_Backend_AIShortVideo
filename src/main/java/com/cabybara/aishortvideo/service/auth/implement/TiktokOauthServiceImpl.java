package com.cabybara.aishortvideo.service.auth.implement;

import com.cabybara.aishortvideo.dto.auth.tiktok.TiktokTokenResponseDTO;
import com.cabybara.aishortvideo.dto.auth.tiktok.TiktokUserInfoDTO;
import com.cabybara.aishortvideo.exception.TiktokApiException;
import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.service.auth.TiktokOauthService;
import com.cabybara.aishortvideo.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

import static com.cabybara.aishortvideo.utils.UtilMethods.toFormData;


@Service
@Slf4j
public class TiktokOauthServiceImpl implements TiktokOauthService {

    @Value("${spring.security.oauth2.client.registration.tiktok.client-id}")
    private String clientKey;

    @Value("${spring.security.oauth2.client.registration.tiktok.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.tiktok.redirect-uri}")
    private String redirectUri;

    private final String fieldsParams = "open_id,avatar_large_url,bio_description,profile_deep_link";

    private final WebClient tiktokOAuthWebClient;
    private final WebClient tiktokApiWebClient;
    private final UserService userService;

    public TiktokOauthServiceImpl(
            @Qualifier("tiktokOAuthWebClient") WebClient tiktokOAuthWebClient,
            @Qualifier("tiktokApiWebClient") WebClient tiktokApiWebClient,
            UserService userService
    ) {
        this.tiktokOAuthWebClient = tiktokOAuthWebClient;
        this.tiktokApiWebClient = tiktokApiWebClient;
        this.userService = userService;
    }

    @Override
    public TiktokTokenResponseDTO exchangeAuthorizationCode(String code) {
        try {
            Map<String, String> formData = Map.of(
                    "client_key", clientKey,
                    "client_secret", clientSecret,
                    "code", code,
                    "redirect_uri", redirectUri,
                    "grant_type", "authorization_code"
            );

            return tiktokOAuthWebClient.post()
                    .uri("/token/")
                    .bodyValue(toFormData(formData))
                    .retrieve()
                    .bodyToMono(TiktokTokenResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("WebClient error during Tiktok token exchange: {}", e.getResponseBodyAsString(), e);
            throw new TiktokApiException("Tiktok API error");
        } catch (Exception e) {
            log.error("Unexpected error during Tiktok token exchange", e);
            throw new TiktokApiException("Unknown error when exchange tiktok token");
        }
    }

    @Override
    public TiktokTokenResponseDTO refreshAccessToken(String refreshToken) {

        try {
            return tiktokOAuthWebClient.post()
                    .uri("/token/")
                    .body(BodyInserters.fromFormData("client_key", clientKey)
                            .with("client_secret", clientSecret)
                            .with("refresh_token", refreshToken)
                            .with("grant_type", "refresh_token"))
                    .retrieve()
                    .bodyToMono(TiktokTokenResponseDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("WebClient error during token refresh: {}", e.getResponseBodyAsString(), e);
            throw new TiktokApiException("Tiktok API refresh error");
        } catch (Exception e) {
            log.error("Unexpected error during token refresh", e);
            throw new TiktokApiException("Unknown error when refreshing token");
        }
    }

    @Override
    public TiktokUserInfoDTO getUserInfo(String accessToken) {
        try {
            return tiktokApiWebClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/user/info/")
                            .queryParam("fields", fieldsParams)
                            .build()
                    )
                    .headers(h -> h.setBearerAuth(accessToken))
                    .retrieve()
                    .bodyToMono(TiktokUserInfoDTO.class)
                    .block();
        } catch (WebClientResponseException e) {
            log.error("WebClient error during user info: {}", e.getResponseBodyAsString(), e);
            throw new TiktokApiException("Tiktok user info API error");
        } catch (Exception e) {
            log.error("Unexpected error during user info", e);
            throw new TiktokApiException("Unknown error");
        }
    }

    @Override
    public Boolean authenticateWithTiktok(String code, Long userId) {
        // 1. Exchange token
        TiktokTokenResponseDTO tokenResponse = exchangeAuthorizationCode(code);
        if (tokenResponse == null || tokenResponse.getAccessToken() == null) {
            throw new TiktokApiException("Failed to obtain access token from TikTok");
        }

        // 2. Get user info
        TiktokUserInfoDTO userInfo = getUserInfo(tokenResponse.getAccessToken());
        if (userInfo == null || userInfo.getData() == null) {
            throw new TiktokApiException("Failed to obtain user info from TikTok");
        }

        // 3. Save user info and token
        User user = userService.createTiktokUser(userId, userInfo.getData().getUser(), tokenResponse);

        return true;
    }
}
