package com.cabybara.aishortvideo.service.user;

import com.cabybara.aishortvideo.dto.user.UserFollowerDTO;
import com.cabybara.aishortvideo.model.User;

import java.util.Set;

public interface UserFollowerService {
    void follow(Long userId, Long followerId);
    void unfollow(Long userId, Long followerId);
    Set<UserFollowerDTO> getFollowing(Long userId);
    Set<UserFollowerDTO> getFollowers(Long userId);
}
