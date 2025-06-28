package com.cabybara.aishortvideo.service.user.implement;

import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserSocialAccount;
import com.cabybara.aishortvideo.repository.UserSocialAccountRepository;
import com.cabybara.aishortvideo.service.user.UserSocialAccountService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserSocialAccountServiceImpl implements UserSocialAccountService {

    private final UserSocialAccountRepository userSocialAccountRepository;

    public UserSocialAccountServiceImpl(UserSocialAccountRepository userSocialAccountRepository) {
        this.userSocialAccountRepository = userSocialAccountRepository;
    }

    @Override
    public void saveUpdateSocialAccount(User user, String platform, String platformUserId, String accessToken, String refreshToken, Instant expiresAt, String scope, String tokenType) {
        UserSocialAccount userSocialAccount = userSocialAccountRepository
                .findByUserAndPlatform(user, platform)
                .orElseGet(UserSocialAccount::new);

        userSocialAccount.setUser(user);
        userSocialAccount.setPlatform(platform);
        userSocialAccount.setPlatformUserId(platformUserId);
        userSocialAccount.setAccessToken(accessToken);
        userSocialAccount.setRefreshToken(refreshToken);
        userSocialAccount.setExpiresAt(expiresAt);
        userSocialAccount.setScope(scope);
        userSocialAccount.setTokenType(tokenType);

        userSocialAccountRepository.save(userSocialAccount);
    }
}
