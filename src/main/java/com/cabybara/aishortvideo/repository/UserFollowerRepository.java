package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserFollower;
import com.cabybara.aishortvideo.model.composite_id.UserFollowerId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface UserFollowerRepository extends JpaRepository<UserFollower, UserFollowerId> {
    @Query("SELECT uf.followingUser FROM UserFollower uf WHERE uf.followerUser.id = :userId")
    Page<User> findAllUsersIFollow(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT uf.followerUser FROM UserFollower uf WHERE uf.followingUser.id = :userId")
    Page<User> findAllUsersFollowingMe(@Param("userId") Long userId, Pageable pageable);

    Long countByUserFollowerIdUserId(Long userId);

    Long countByUserFollowerIdFollowerId(Long userId);
}
