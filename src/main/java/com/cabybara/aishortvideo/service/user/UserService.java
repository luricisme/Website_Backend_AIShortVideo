package com.cabybara.aishortvideo.service.user;

import com.cabybara.aishortvideo.dto.auth.*;
import com.cabybara.aishortvideo.dto.auth.tiktok.TiktokTokenResponseDTO;
import com.cabybara.aishortvideo.dto.auth.tiktok.TiktokUserDTO;
import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
import com.cabybara.aishortvideo.dto.user.AdminUserGrowthDTO;
import com.cabybara.aishortvideo.dto.user.AdminUsersOverviewDTO;
import com.cabybara.aishortvideo.dto.user.UpdateUserDTO;
import com.cabybara.aishortvideo.dto.user.UserDTO;
import com.cabybara.aishortvideo.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface UserService extends UserDetailsService  {
    RegisterResponseDTO addUser(RegisterRequestDTO registerRequestDTO);

    UserDTO loadUserById(Long id);

    UserDTO updateUser(Long id, UpdateUserDTO updateUserDTO);

    void deleteUser(Long id);

    User findOrCreateGoogleUser(GoogleUserInfoDTO userInfo, GoogleTokenResponseDTO tokenResponse);

    User updateOrCreateTiktokToken(Long userId, TiktokTokenResponseDTO tokenResponse);

    User createTiktokUser(Long userId, TiktokUserDTO userInfoDTO, TiktokTokenResponseDTO tokenResponseDTO);

    String updateAvatar(MultipartFile avatar, Long userId) throws IOException;

    AdminUsersOverviewDTO getOverviewUsers();

    PageResponseDetail<Object> getAllUser(String name, String status, String sortProp, String sortDirect, int page, int pageSize);

    AdminUserGrowthDTO getGrowthMetrics(String periodType, Long numPeriod);

    RegisterResponseDTO addAdminUser(RegisterAdminDTO registerAdminDTO);
}
