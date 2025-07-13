package com.cabybara.aishortvideo.mapper;

import com.cabybara.aishortvideo.dto.auth.RegisterRequestDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterResponseDTO;
import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
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
    @Mapping(source = "updatedAt", target = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    UserDTO toUserDTO(User user);

    @Named("setUserFollowerToString")
    static PageResponseDetail<?> setUserFollowerToString(Set<UserFollower> followers) {
        Set<UserFollowerDTO> userFollowerDTOS = followers.stream()
                .map(UserFollower::getFollowerUser)
                .map(user -> new UserFollowerDTO(user.getId(), user.getUsername()))
                .collect(Collectors.toSet());

        return PageResponseDetail.builder()
                .pageNo(0)
                .pageSize(5)
                .totalPage(userFollowerDTOS.size() / 5)
                .totalElements((long) userFollowerDTOS.size())
                .build();
    }

    @Named("setUserFollowingToString")
    static PageResponseDetail<?> setUserFollowingToString(Set<UserFollower> following) {
        Set<UserFollowerDTO> userFollowingDTOS = following.stream()
                .map(UserFollower::getFollowingUser)
                .map(user -> new UserFollowerDTO(user.getId(), user.getUsername()))
                .collect(Collectors.toSet());

        return PageResponseDetail.builder()
                .pageNo(0)
                .pageSize(5)
                .totalPage(userFollowingDTOS.size() / 5)
                .totalElements((long) userFollowingDTOS.size())
                .build();
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "youtube", target = "youtube")
    void updateFromDto(UpdateUserDTO dto, @MappingTarget User entity);

    RegisterResponseDTO toRegisterResponseDTO(User user);
}
