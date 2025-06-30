package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserSocialAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserSocialAccountRepository extends JpaRepository<UserSocialAccount, Long> {
    Optional<UserSocialAccount> findByUserAndPlatform(User user, String platform);

    List<UserSocialAccount> findAllByUser(User user);

    Optional<UserSocialAccount> findByPlatformUserIdAndPlatform(String platformUserId, String platform);
}
