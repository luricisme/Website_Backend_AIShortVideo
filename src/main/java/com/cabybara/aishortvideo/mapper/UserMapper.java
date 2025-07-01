package com.cabybara.aishortvideo.mapper;

import com.cabybara.aishortvideo.dto.auth.RegisterRequestDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterResponseDTO;
import com.cabybara.aishortvideo.dto.user.UpdateUserDTO;
import com.cabybara.aishortvideo.dto.user.UserDTO;
import com.cabybara.aishortvideo.dto.user.UserFollowerDTO;
import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserFollower;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "status", expression = "java(com.cabybara.aishortvideo.utils.UserStatus.ACTIVE)")
    User toUser(RegisterRequestDTO userDTO);

    @Mapping(source = "followers", target = "followers", qualifiedByName = "setUserFollowerToString")
    @Mapping(source = "followings", target = "followings", qualifiedByName = "setUserFollowingToString")
    UserDTO toUserDTO(User user);

    @Named("setUserFollowerToString")
    static Set<UserFollowerDTO> setUserFollowerToString(Set<UserFollower> followers) {
        if (followers == null) {
            return Collections.emptySet();
        }

        return followers.stream()
                .map(UserFollower::getFollowerUser)
                .map(user -> new UserFollowerDTO(user.getId(), user.getUsername()))
                .collect(Collectors.toSet());
    }

    @Named("setUserFollowingToString")
    static Set<UserFollowerDTO> setUserFollowingToString(Set<UserFollower> following) {
        if (following == null) {
            return Collections.emptySet();
        }

        return following.stream()
                .map(UserFollower::getFollowingUser)
                .map(user -> new UserFollowerDTO(user.getId(), user.getUsername()))
                .collect(Collectors.toSet());
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateUserDTO dto, @MappingTarget User entity);

    RegisterResponseDTO toRegisterResponseDTO(User user);
}
