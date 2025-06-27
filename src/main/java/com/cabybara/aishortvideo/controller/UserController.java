package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.dto.user.UpdateUserDTO;
import com.cabybara.aishortvideo.dto.user.UserDTO;
import com.cabybara.aishortvideo.exception.ErrorResponse;
import com.cabybara.aishortvideo.service.user.implement.UserServiceImpl;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users Apis")
@Validated
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
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
    public ResponseData<UserDTO> getUser(@PathVariable("id") Long id) {
        UserDTO userDTO = userService.loadUserById(id);
        return new ResponseData<>(HttpStatus.OK, "Successfully", userDTO);
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
    public ResponseData<UserDTO> updateUser(@PathVariable("id") Long id, @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        UserDTO updatedUserDTO = userService.updateUser(id, updateUserDTO);
        return new ResponseData<>(HttpStatus.OK, "Successfully", updatedUserDTO);
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
    public ResponseData<String> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return new ResponseData<>(HttpStatus.OK, "Successfully", null);
    }
}
