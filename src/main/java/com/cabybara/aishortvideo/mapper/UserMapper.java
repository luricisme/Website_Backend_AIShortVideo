package com.cabybara.aishortvideo.mapper;

import com.cabybara.aishortvideo.dto.auth.RegisterRequestDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterResponseDTO;
import com.cabybara.aishortvideo.dto.user.UpdateUserDTO;
import com.cabybara.aishortvideo.dto.user.UserDTO;
import com.cabybara.aishortvideo.model.User;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "status", expression = "java(com.cabybara.aishortvideo.utils.UserStatus.ACTIVE)")
    User toUser(RegisterRequestDTO userDTO);

    UserDTO toUserDTO(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateFromDto(UpdateUserDTO dto, @MappingTarget User entity);

    RegisterResponseDTO toRegisterResponseDTO(User user);
}
