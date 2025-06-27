package com.cabybara.aishortvideo.service.user.implement;

import com.cabybara.aishortvideo.dto.auth.RegisterRequestDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterResponseDTO;
import com.cabybara.aishortvideo.dto.user.UpdateUserDTO;
import com.cabybara.aishortvideo.dto.user.UserDTO;
import com.cabybara.aishortvideo.exception.UserAlreadyExistsException;
import com.cabybara.aishortvideo.exception.UserNotFoundException;
import com.cabybara.aishortvideo.mapper.UserMapper;
import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserDetail;
import com.cabybara.aishortvideo.repository.UserRepository;
import com.cabybara.aishortvideo.service.user.UserService;
import com.cabybara.aishortvideo.utils.UserStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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

    @Cacheable(value = "user", key = "#id")
    @Override
    public UserDTO loadUserById(Long id) {
        User user = userRepository.findByIdAndStatus(id, UserStatus.ACTIVE)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        return userMapper.toUserDTO(user);
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
        User existedUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));

        existedUser.setStatus(UserStatus.DELETED);
        userRepository.save(existedUser);
    }
}
