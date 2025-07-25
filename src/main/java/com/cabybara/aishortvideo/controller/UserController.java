package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.user.UpdateUserDTO;
import com.cabybara.aishortvideo.dto.user.UserDTO;
import com.cabybara.aishortvideo.dto.user.UserFollowerDTO;
import com.cabybara.aishortvideo.exception.ErrorResponse;
import com.cabybara.aishortvideo.model.UserDetail;
import com.cabybara.aishortvideo.service.user.UserFollowerService;
import com.cabybara.aishortvideo.service.user.implement.UserServiceImpl;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping("/users")
@Tag(name = "Users Apis")
@Validated
public class UserController {
    private final UserServiceImpl userService;
    private final UserFollowerService userFollowerService;

    public UserController(
            UserServiceImpl userService,
            UserFollowerService userFollowerService
    ) {
        this.userService = userService;
        this.userFollowerService = userFollowerService;
    }

    @GetMapping("{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ResponseData<UserDTO>> getUser(@PathVariable("id") Long id) {
        UserDTO userDTO = userService.loadUserById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", userDTO));
    }

    @PutMapping("/{id}")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseEntity<ResponseData<UserDTO>> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        UserDTO updatedUserDTO = userService.updateUser(id, updateUserDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", updatedUserDTO));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/avatar")
    public ResponseEntity<ResponseData<String>> updateAvatar(
            @RequestParam("avatar")MultipartFile avatar,
            @AuthenticationPrincipal UserDetail userDetail
    ) throws IOException {
        String avatarUrl = userService.updateAvatar(avatar, userDetail.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", avatarUrl));
    }

    @GetMapping("{id}/follower")
    public ResponseEntity<ResponseData<?>> getFollowers(
            @Parameter(description = "id of user want to get follower",
                    required = true,
                    example = "1")
            @PathVariable("id") Long userId,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize
    ) {
        PageResponseDetail<Object> followerUsers = userFollowerService.getFollowers(userId, page, pageSize);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Sucessfully", followerUsers));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<ResponseData<?>> getFollowings(
            @Parameter(description = "id of user want to get following",
                    required = true,
                    example = "1")
            @PathVariable("id") Long userId,
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize
    ) {
        PageResponseDetail<?> followingUsers = userFollowerService.getFollowing(userId, page, pageSize);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", followingUsers));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/follow")
    public ResponseEntity<ResponseData<String>> addUserFollower(
            @Parameter(description = "id of user want to follow",
                    required = true,
                    example = "1")
            @PathVariable("id") Long userId,
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        userFollowerService.follow(userId, userDetail.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", null));
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("{id}/follow")
    public ResponseEntity<ResponseData<String>> unfollowUser(
            @Parameter(description = "id of user want to unfollow",
                    required = true,
                    example = "1")
            @PathVariable("id") Long userId,
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        userFollowerService.unfollow(userId, userDetail.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", null));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/is-following")
    public ResponseEntity<ResponseData<Boolean>> isFollowing(
            @Parameter(description = "Id of the user to check follow status",
                    required = true,
                    example = "1")
            @PathVariable("id") Long userId,
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        Boolean isFollowing = userFollowerService.isFollowing(userDetail.getId(), userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", isFollowing));
    }
}
