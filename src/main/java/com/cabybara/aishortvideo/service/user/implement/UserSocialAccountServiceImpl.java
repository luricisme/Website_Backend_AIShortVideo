package com.cabybara.aishortvideo.service.user.implement;

import com.cabybara.aishortvideo.exception.UserSocialAccountNotFoundException;
import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserSocialAccount;
import com.cabybara.aishortvideo.repository.UserRepository;
import com.cabybara.aishortvideo.repository.UserSocialAccountRepository;
import com.cabybara.aishortvideo.service.user.UserSocialAccountService;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserSocialAccountServiceImpl implements UserSocialAccountService {

    private final UserSocialAccountRepository userSocialAccountRepository;
    private final UserRepository userRepository;

    public UserSocialAccountServiceImpl(
            UserSocialAccountRepository userSocialAccountRepository,
            UserRepository userRepository
    ) {
        this.userSocialAccountRepository = userSocialAccountRepository;
        this.userRepository = userRepository;
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

    @Override
    public void updateAccessToken(User user, String platform, String token) {
        userSocialAccountRepository
                .findByUserAndPlatform(user, platform)
                .ifPresent(account -> {
                    account.setAccessToken(token);
                    account.setExpiresAt(Instant.now().plusSeconds(3600));
                    userSocialAccountRepository.save(account);
                });
    }

    @Override
    public void updateAccessTokenAndExpiry(String platformUserId, String platform, String token, Instant expiredAt) {
        userSocialAccountRepository
                .findByPlatformUserIdAndPlatform(platformUserId, platform)
                .ifPresent(account -> {
                    account.setAccessToken(token);
                    account.setExpiresAt(expiredAt);
                    userSocialAccountRepository.save(account);
                });
    }

    @Override
    public UserSocialAccount getUserSocialAccount(Long userId, String platform) {
        User user = userRepository.getReferenceById(userId);
        return userSocialAccountRepository.findByUserAndPlatform(user, platform)
                .orElseThrow(
                        () -> new UserSocialAccountNotFoundException("Social account not found! Please login with google to continue")
                );
    }
}
