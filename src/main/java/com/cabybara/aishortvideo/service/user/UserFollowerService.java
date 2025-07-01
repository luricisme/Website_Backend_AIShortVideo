package com.cabybara.aishortvideo.service.user;

import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
import com.cabybara.aishortvideo.dto.user.UserFollowerDTO;

import java.util.Set;

public interface UserFollowerService {
    void follow(Long userId, Long followerId);
    void unfollow(Long userId, Long followerId);
    PageResponseDetail<Object> getFollowing(Long userId, int page, int pageSize);
    PageResponseDetail<Object> getFollowers(Long userId, int page, int pageSize);
}
