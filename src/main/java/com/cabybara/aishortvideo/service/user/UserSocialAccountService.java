package com.cabybara.aishortvideo.service.user;

import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserSocialAccount;

import java.time.Instant;
import java.util.Map;

public interface UserSocialAccountService {
    void saveUpdateSocialAccount(User user,
                                 String platform,
                                 String platformUserId,
                                 String accessToken,
                                 String refreshToken,
                                 Instant expiresAt,
                                 String scope,
                                 String tokenType);

    void updateAccessToken(User user, String platform, String token);
    void updateAccessTokenAndExpiry(String platformUserId, String platform, String token, Instant expiredAt);
    UserSocialAccount getUserSocialAccount(Long userId, String platform);
}
