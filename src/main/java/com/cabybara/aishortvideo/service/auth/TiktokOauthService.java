package com.cabybara.aishortvideo.service.auth;

import com.cabybara.aishortvideo.dto.auth.*;
import com.cabybara.aishortvideo.dto.auth.tiktok.TiktokTokenResponseDTO;
import com.cabybara.aishortvideo.dto.auth.tiktok.TiktokUserInfoDTO;

public interface TiktokOauthService {
    TiktokTokenResponseDTO exchangeAuthorizationCode(String code);
    TiktokTokenResponseDTO refreshAccessToken(String refreshToken);
    TiktokUserInfoDTO getUserInfo(String accessToken);
    Boolean authenticateWithTiktok(String code, Long userId);
}
