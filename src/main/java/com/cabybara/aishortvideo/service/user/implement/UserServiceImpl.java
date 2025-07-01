package com.cabybara.aishortvideo.service.user.implement;

import com.cabybara.aishortvideo.dto.auth.GoogleTokenResponseDTO;
import com.cabybara.aishortvideo.dto.auth.GoogleUserInfoDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterRequestDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterResponseDTO;
import com.cabybara.aishortvideo.dto.user.UpdateUserDTO;
import com.cabybara.aishortvideo.dto.user.UserDTO;
import com.cabybara.aishortvideo.dto.user.UserFollowerDTO;
import com.cabybara.aishortvideo.exception.UserAlreadyExistsException;
import com.cabybara.aishortvideo.exception.UserNotFoundException;
import com.cabybara.aishortvideo.mapper.UserMapper;
import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserDetail;
import com.cabybara.aishortvideo.model.UserFollower;
import com.cabybara.aishortvideo.repository.UserFollowerRepository;
import com.cabybara.aishortvideo.repository.UserRepository;
import com.cabybara.aishortvideo.service.user.UserService;
import com.cabybara.aishortvideo.service.user.UserSocialAccountService;
import com.cabybara.aishortvideo.utils.UserRole;
import com.cabybara.aishortvideo.utils.UserStatus;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserSocialAccountService userSocialAccountService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserFollowerRepository userFollowerRepository;

    public UserServiceImpl(
            UserRepository userRepository,
            UserSocialAccountService userSocialAccountService,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            UserFollowerRepository userFollowerRepository
    ) {
        this.userRepository = userRepository;
        this.userSocialAccountService = userSocialAccountService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userFollowerRepository = userFollowerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmailAndStatus(username, UserStatus.ACTIVE);

        return user.map(UserDetail::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Override
    public RegisterResponseDTO addUser(RegisterRequestDTO registerRequestDTO) {
        Optional<User> existedUserOptional = userRepository.findByEmail(registerRequestDTO.getEmail());

        if (existedUserOptional.isPresent()) {
            User existedUser = existedUserOptional.get();
            throw new UserAlreadyExistsException("User with email " + existedUser.getEmail() + " already exists.");
        }

        User user = userMapper.toUser(registerRequestDTO);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return userMapper.toRegisterResponseDTO(user);
    }

    @Cacheable(value = "users", key = "#id")
    @Override
    @Transactional
    public UserDTO loadUserById(Long id) {
        User user = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        UserDTO userDTO = userMapper.toUserDTO(user);
        Set<User> followers = userFollowerRepository.findAllUsersFollowingMe(id);
        Set<User> followings = userFollowerRepository.findAllUsersIFollow(id);
        userDTO.setFollowers(followers.stream()
                .map(u -> new UserFollowerDTO(u.getId(), u.getUsername()))
                .collect(Collectors.toSet()));
        userDTO.setFollowings(followings.stream()
                .map(u -> new UserFollowerDTO(u.getId(), u.getUsername()))
                .collect(Collectors.toSet()));

        return userDTO;
    }

    @CachePut(value = "users", key = "#id")
    @Override
    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User existingUser = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        userMapper.updateFromDto(updateUserDTO, existingUser);

        User updatedUser = userRepository.save(existingUser);

        return userMapper.toUserDTO(updatedUser);
    }

    @CacheEvict(value = "users", key = "#id")
    @Override
    public void deleteUser(Long id) {
        User existedUser = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        existedUser.setStatus(UserStatus.DELETED);
        userRepository.save(existedUser);
    }

    @Override
    public User findOrCreateGoogleUser(GoogleUserInfoDTO userInfo, GoogleTokenResponseDTO tokenResponse) {
        User user = userRepository.findByEmailAndStatus(userInfo.getEmail(), UserStatus.ACTIVE)
                .orElseGet(()  -> {
                    User newUser =  User.builder()
                            .email(userInfo.getEmail())
                            .firstName(userInfo.getGiven_name())
                            .lastName(userInfo.getFamily_name())
                            .username(userInfo.getName())
                            .role(UserRole.USER)
                            .status(UserStatus.ACTIVE)
                            .avatar(userInfo.getPicture())
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();

                    return userRepository.save(newUser);
                });

        userSocialAccountService.saveUpdateSocialAccount(
                user,
                "google",
                userInfo.getSub(),
                tokenResponse.getAccess_token(),
                tokenResponse.getRefresh_token(),
                Instant.now().plusSeconds(Long.parseLong(tokenResponse.getExpires_in())),
                tokenResponse.getScope(),
                tokenResponse.getToken_type()
        );

        return user;
    }
}
