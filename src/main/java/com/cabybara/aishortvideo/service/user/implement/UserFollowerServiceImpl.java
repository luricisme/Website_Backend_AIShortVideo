package com.cabybara.aishortvideo.service.user.implement;

import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
import com.cabybara.aishortvideo.dto.user.UserFollowerDTO;
import com.cabybara.aishortvideo.exception.UserFollowerException;
import com.cabybara.aishortvideo.exception.UserNotFoundException;
import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserFollower;
import com.cabybara.aishortvideo.model.composite_id.UserFollowerId;
import com.cabybara.aishortvideo.repository.UserFollowerRepository;
import com.cabybara.aishortvideo.repository.UserRepository;
import com.cabybara.aishortvideo.service.user.UserFollowerService;
import com.cabybara.aishortvideo.utils.UserStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
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

        UserFollowerId userFollowerId = new UserFollowerId(followerId, userId);
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

        UserFollowerId id = new UserFollowerId(followerId, userId);
        UserFollower userFollower = userFollowerRepository.findById(id)
                .orElseThrow(() -> new UserFollowerException(HttpStatus.NOT_FOUND, "Follow relationship not found"));

        userFollowerRepository.delete(userFollower);
    }

    @Override
    public PageResponseDetail<Object> getFollowing(Long userId, int page, int pageSize) {
//        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
//                .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//        return user.getFollowing().stream()
//                .map(UserFollower::getFollowingUser)
//                .collect(Collectors.toSet());

        if (page > 0) {
            page = page - 1;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").descending());

        Page<User> userFollowing = userFollowerRepository.findAllUsersIFollow(userId, pageable);

        Set<UserFollowerDTO> userFollowerDTOS =  userFollowing.stream()
                .map(user -> new UserFollowerDTO(user.getId(), user.getUsername()))
                .collect(Collectors.toSet());

        return PageResponseDetail.builder()
                .pageNo(page)
                .pageSize(pageSize)
                .totalPage(userFollowing.getTotalPages())
                .totalElements(userFollowing.getTotalElements())
                .items(userFollowerDTOS)
                .build();
    }

    @Override
    public PageResponseDetail<Object> getFollowers(Long userId, int page, int pageSize) {
//        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
//                .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//        return user.getFollowers().stream()
//                .map(UserFollower::getFollowerUser)
//                .collect(Collectors.toSet());

        if (page > 0) {
            page = page - 1;
        }

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").descending());

        Page<User> userFollowers = userFollowerRepository.findAllUsersFollowingMe(userId, pageable);

        Set<UserFollowerDTO> userFollowerDTOS =  userFollowers.stream()
                .map(user -> new UserFollowerDTO(user.getId(), user.getUsername()))
                .collect(Collectors.toSet());

        return PageResponseDetail.builder()
                .pageNo(page)
                .pageSize(pageSize)
                .totalPage(userFollowers.getTotalPages())
                .totalElements(userFollowers.getTotalElements())
                .items(userFollowerDTOS)
                .build();
    }

    @Override
    public Boolean isFollowing(Long followerId, Long followingUserId) {
        return userFollowerRepository.existsById(new UserFollowerId(followerId, followingUserId));
    }
}
