package com.cabybara.aishortvideo.service.user;

import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserSocialAccount;

import java.time.Instant;

public interface UserSocialAccountService {
    void saveUpdateSocialAccount(User user,
                                 String platform,
                                 String platformUserId,
                                 String accessToken,
                                 String refreshToken,
                                 Instant expiresAt,
                                 String scope,
                                 String tokenType);
}
