package com.cabybara.aishortvideo.service.user.implement;

import com.cabybara.aishortvideo.exception.UserFollowerException;
import com.cabybara.aishortvideo.exception.UserNotFoundException;
import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserFollower;
import com.cabybara.aishortvideo.model.composite_id.UserFollowerId;
import com.cabybara.aishortvideo.repository.UserFollowerRepository;
import com.cabybara.aishortvideo.repository.UserRepository;
import com.cabybara.aishortvideo.service.user.UserFollowerService;
import com.cabybara.aishortvideo.utils.UserStatus;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserFollowerServiceImpl implements UserFollowerService {
    private final UserFollowerRepository userFollowerRepository;
    private final UserRepository userRepository;

    public UserFollowerServiceImpl(
            UserFollowerRepository userFollowerRepository,
            UserRepository userRepository
    ) {
        this.userFollowerRepository = userFollowerRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void follow(Long userId, Long followerId) {
        if (Objects.equals(userId, followerId)) {
            throw new UserFollowerException(HttpStatus.BAD_REQUEST, "Cannot follow yourself");
        }

        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        User followerUser = userRepository.findByIdAndStatus(followerId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("Follower user not found"));

        UserFollowerId userFollowerId = new UserFollowerId(userId, followerId);
        if (userFollowerRepository.existsById(userFollowerId)) {
            throw new UserFollowerException(HttpStatus.CONFLICT, "User is already followed");
        }

        UserFollower userFollower = UserFollower.builder()
                .userFollowerId(userFollowerId)
                .followerUser(followerUser)
                .followingUser(user)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userFollowerRepository.save(userFollower);
    }

    @Override
    public void unfollow(Long userId, Long followerId) {
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        User followerUser = userRepository.findByIdAndStatus(followerId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("Follower user not found"));

        UserFollowerId id = new UserFollowerId(userId, followerId);
        UserFollower userFollower = userFollowerRepository.findById(id)
                .orElseThrow(() -> new UserFollowerException(HttpStatus.NOT_FOUND, "Follow relationship not found"));

        userFollowerRepository.delete(userFollower);
    }

    @Override
    public Set<User> getFollowing(Long userId) {
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<UserFollower> list = userFollowerRepository.findAll();
        for (UserFollower f : list) {
            System.out.println("follower: " + f.getFollowerUser().getId() + ", following: " + f.getFollowingUser().getId());
        }

        System.out.println(user.getFollowing());

        return user.getFollowing().stream()
                .map(UserFollower::getFollowingUser)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> getFollowers(Long userId) {
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return user.getFollowers().stream()
                .map(UserFollower::getFollowerUser)
                .collect(Collectors.toSet());
    }
}
