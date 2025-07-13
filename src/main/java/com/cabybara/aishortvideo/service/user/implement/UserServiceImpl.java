package com.cabybara.aishortvideo.service.user.implement;

import com.cabybara.aishortvideo.dto.auth.*;
import com.cabybara.aishortvideo.dto.auth.tiktok.TiktokTokenResponseDTO;
import com.cabybara.aishortvideo.dto.auth.tiktok.TiktokUserDTO;
import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
import com.cabybara.aishortvideo.dto.user.*;
import com.cabybara.aishortvideo.exception.InvalidParamsException;
import com.cabybara.aishortvideo.exception.UserAlreadyExistsException;
import com.cabybara.aishortvideo.exception.UserNotFoundException;
import com.cabybara.aishortvideo.mapper.UserMapper;
import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserDetail;
import com.cabybara.aishortvideo.repository.UserFollowerRepository;
import com.cabybara.aishortvideo.repository.UserRepository;
import com.cabybara.aishortvideo.repository.VideoRepository;
import com.cabybara.aishortvideo.service.create_video.SaveFileService;
import com.cabybara.aishortvideo.service.user.UserService;
import com.cabybara.aishortvideo.service.user.UserSocialAccountService;
import com.cabybara.aishortvideo.utils.UserRole;
import com.cabybara.aishortvideo.utils.UserStatus;
import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
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
    private final SaveFileService saveFileService;
    private final VideoRepository videoRepository;
    private final List<String> userSortProperties = List.of("id", "firstName", "lastName", "email", "status", "createdAt", "updatedAt");

    public UserServiceImpl(
            UserRepository userRepository,
            UserSocialAccountService userSocialAccountService,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            UserFollowerRepository userFollowerRepository,
            SaveFileService saveFileService,
            VideoRepository videoRepository) {
        this.userRepository = userRepository;
        this.userSocialAccountService = userSocialAccountService;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.userFollowerRepository = userFollowerRepository;
        this.saveFileService = saveFileService;
        this.videoRepository = videoRepository;
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
        int page = 0;
        int pageSize = 5;
        User user = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        UserDTO userDTO = userMapper.toUserDTO(user);

        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("id").descending());
        Page<User> followers = userFollowerRepository.findAllUsersFollowingMe(id, pageable);
        Page<User> followings = userFollowerRepository.findAllUsersIFollow(id, pageable);

        Set<UserFollowerDTO> userFollowerDTOS =  followers.stream()
                .map(u -> new UserFollowerDTO(u.getId(), u.getUsername()))
                .collect(Collectors.toSet());

        Set<UserFollowerDTO> userFollowingDTOS =  followings.stream()
                .map(u -> new UserFollowerDTO(u.getId(), u.getUsername()))
                .collect(Collectors.toSet());

        userDTO.setFollowers(PageResponseDetail.builder()
                .pageNo(page)
                .pageSize(pageSize)
                .totalPage(followers.getTotalPages())
                .totalElements(followers.getTotalElements())
                .items(userFollowerDTOS)
                .build());
        userDTO.setFollowings(PageResponseDetail.builder()
                .pageNo(page)
                .pageSize(pageSize)
                .totalPage(followings.getTotalPages())
                .totalElements(followings.getTotalElements())
                .items(userFollowingDTOS)
                .build());

        return userDTO;
    }

    @CachePut(value = "users", key = "#id")
    @Override
    public UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User existingUser = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        userMapper.updateFromDto(updateUserDTO, existingUser);
        existingUser.setUpdatedAt(LocalDateTime.now());

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

    @CachePut(value = "users", key = "#userId")
    @Override
    public User updateOrCreateTiktokToken(Long userId, TiktokTokenResponseDTO tokenResponse) {
        User user = userRepository.getReferenceById(userId);

        userSocialAccountService.saveUpdateSocialAccount(
                user,
                "tiktok",
                tokenResponse.getOpenId(),
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                Instant.now().plusSeconds(tokenResponse.getExpiresIn()),
                tokenResponse.getScope(),
                tokenResponse.getTokenType()
        );

        return user;
    }

    @Override
    public User createTiktokUser(Long userId, TiktokUserDTO userDTO, TiktokTokenResponseDTO tokenResponse) {
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userSocialAccountService.saveUpdateSocialAccount(
                user,
                "tiktok",
                userDTO.getOpen_id(),
                tokenResponse.getAccessToken(),
                tokenResponse.getRefreshToken(),
                Instant.now().plusSeconds(tokenResponse.getExpiresIn()),
                tokenResponse.getScope(),
                tokenResponse.getTokenType()
        );

        return user;
    }

    @Override
    public String updateAvatar(MultipartFile avatar, Long userId) throws IOException {
        User user = userRepository.findByIdAndStatus(userId, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        String fileUrl = saveFileService.uploadAvatar(avatar, userId, "avatar");
        user.setAvatar(fileUrl);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return fileUrl;
    }

    @Override
    public AdminUsersOverviewDTO getOverviewUsers() {
        return userRepository.getOverviewUsers();
    }

    private boolean isValidSortProperty(String sortProp) {
        return userSortProperties.contains(sortProp);
    }

    @Override
    public PageResponseDetail<Object> getAllUser(String name, String status, String sortProp, String sortDirect, int page, int pageSize) {
        if (page > 0) {
            page = page - 1;
        }

        String sortBy;
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDirect) ? Sort.Direction.DESC : Sort.Direction.ASC;
        if (sortProp != null) {
            if (!isValidSortProperty(sortProp)) {
                throw new InvalidParamsException("Invalid sort property. Allowed values are: " + String.join(", ", userSortProperties));
            }
            sortBy = sortProp;
        } else {
            sortBy = "id";
        }
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by(direction, sortBy));

        UserStatus userStatus = null;
        if (status != null && !"ALL".equalsIgnoreCase(status)) {
            try {
                userStatus = UserStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidParamsException("Invalid status value: '" + status + "'. Allowed values are: " + List.of(UserStatus.values()) + " or ALL.");
            }
        }

        Page<User> userDTOPage;
        if ((name != null && !name.isEmpty()) && userStatus != null) {
            userDTOPage = userRepository.findByNameContainingIgnoreCaseAndStatus(name, userStatus, pageable);
        } else if ((name != null && !name.isEmpty())) {
            userDTOPage = userRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (userStatus != null) {
            userDTOPage = userRepository.findByStatus(userStatus, pageable);
        } else {
            userDTOPage = userRepository.findAll(pageable);
        }

        List<AdminUsersDTO> adminUsersDTOList = userDTOPage.stream()
                .map(user -> {
                    AdminUsersDTO dto = userMapper.toAdminUsersDTO(user);
                    dto.setTotalVideo(videoRepository.countByUserId(dto.getId()));
                    return dto;
                })
                .toList();

        return PageResponseDetail.builder()
                .pageNo(page)
                .pageSize(pageSize)
                .totalPage(userDTOPage.getTotalPages())
                .totalElements(userDTOPage.getTotalElements())
                .items(adminUsersDTOList)
                .build();
    }

    @Override
    public AdminUserGrowthDTO getGrowthMetrics(String periodType, Long numPeriod) {
        Long totalUser = userRepository.count();
        Long previousUserCount = 0L;
        Long followingUserCount = 0L;
        LocalDateTime periodStart = LocalDateTime.now();
        LocalDateTime periodEnd = LocalDateTime.now();

        periodStart = switch (periodType.toLowerCase()) {
            case "day" -> LocalDateTime.now().minusDays(numPeriod).toLocalDate().atStartOfDay();
            case "week" -> LocalDateTime.now().minusWeeks(numPeriod).toLocalDate().atStartOfDay();
            case "month" -> LocalDateTime.now().minusMonths(numPeriod).toLocalDate().atStartOfDay();
            default -> periodStart;
        };

        previousUserCount = userRepository.countByCreatedAtBefore(periodStart);
        followingUserCount = totalUser - previousUserCount;
        double growthPercent = previousUserCount == 0 ? 100.0
                : ((double) (followingUserCount) / previousUserCount) * 100;

        return AdminUserGrowthDTO.builder()
                .totalUser(totalUser)
                .periodStart(periodStart)
                .periodEnd(periodEnd)
                .previousUserCount(previousUserCount)
                .followingUserCount(followingUserCount)
                .growthPercent(growthPercent)
                .build();
    }

    @Override
    public RegisterResponseDTO addAdminUser(RegisterAdminDTO registerAdminDTO) {
        Optional<User> existedUserOptional = userRepository.findByEmail(registerAdminDTO.getEmail());

        if (existedUserOptional.isPresent()) {
            User existedUser = existedUserOptional.get();
            throw new UserAlreadyExistsException("User with email " + existedUser.getEmail() + " already exists.");
        }

        User user = userMapper.toUser(registerAdminDTO);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        return userMapper.toRegisterResponseDTO(user);
    }


}
