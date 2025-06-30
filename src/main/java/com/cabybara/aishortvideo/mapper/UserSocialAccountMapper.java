package com.cabybara.aishortvideo.mapper;

import com.cabybara.aishortvideo.dto.auth.GoogleTokenResponseDTO;
import com.cabybara.aishortvideo.model.UserSocialAccount;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserSocialAccountMapper {
    UserSocialAccountMapper INSTANCE = Mappers.getMapper(UserSocialAccountMapper.class);

    GoogleTokenResponseDTO toGoogleTokenResponseDTO(UserSocialAccount userSocialAccount);
}
