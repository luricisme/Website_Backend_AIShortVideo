package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.dto.user.UpdateUserDTO;
import com.cabybara.aishortvideo.dto.user.UserDTO;
import com.cabybara.aishortvideo.service.user.implement.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    private final UserServiceImpl userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("{id}")
    public ResponseData<UserDTO> getUser(@PathVariable("id") Long id) {
        UserDTO userDTO = userService.loadUserById(id);
        if (userDTO == null) {
            return new ResponseError<>(HttpStatus.NOT_FOUND, "User not found");
        }
        return new ResponseData<>(HttpStatus.OK, "Successfully", userDTO);
    }

    @PutMapping("/{id}")
    public ResponseData<UserDTO> updateUser(@PathVariable("id") Long id, @RequestBody UpdateUserDTO updateUserDTO) {
        UserDTO updatedUserDTO = userService.updateUser(id, updateUserDTO);

        if (updatedUserDTO == null) {
            return new ResponseError<>(HttpStatus.NOT_FOUND, "User not found");
        }
        return new ResponseData<>(HttpStatus.OK, "Successfully", updatedUserDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseData<String> deleteUser(@PathVariable("id") Long id) {
        userService.deleteUser(id);
        return new ResponseData<>(HttpStatus.OK, "Successfully", null);
    }
}
