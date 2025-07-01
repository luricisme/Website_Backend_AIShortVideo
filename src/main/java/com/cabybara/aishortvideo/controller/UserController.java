package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.dto.user.UpdateUserDTO;
import com.cabybara.aishortvideo.dto.user.UserDTO;
import com.cabybara.aishortvideo.exception.ErrorResponse;
import com.cabybara.aishortvideo.model.User;
import com.cabybara.aishortvideo.model.UserDetail;
import com.cabybara.aishortvideo.service.user.UserFollowerService;
import com.cabybara.aishortvideo.service.user.implement.UserServiceImpl;
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

import java.util.List;
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

    @GetMapping("{id}/follower")
    public ResponseEntity<ResponseData<Set<User>>> getFollowers(
            @PathVariable("id") Long userId
    ) {
        Set<User> followerUsers = userFollowerService.getFollowers(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Sucessfully", followerUsers));
    }

    @GetMapping("/{id}/following")
    public ResponseEntity<ResponseData<Set<User>>> getFollowings(
            @PathVariable("id") Long userId
    ) {
        Set<User> followingUsers = userFollowerService.getFollowing(userId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", followingUsers));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/follow")
    public ResponseEntity<ResponseData<String>> addUserFollower(
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
            @PathVariable("id") Long userId,
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        userFollowerService.unfollow(userId, userDetail.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
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
    public ResponseEntity<ResponseData<String>> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", null));
    }
}
