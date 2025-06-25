package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.auth.LoginRequestDTO;
import com.cabybara.aishortvideo.dto.auth.LoginResponseDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterRequestDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterResponseDTO;
import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.service.implement.JwtServiceImpl;
import com.cabybara.aishortvideo.service.implement.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    private final UserServiceImpl userService;
    private final JwtServiceImpl jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserServiceImpl userService, JwtServiceImpl jwtService, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    public ResponseData<RegisterResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        RegisterResponseDTO user = userService.addUser(registerRequestDTO);
        if (user != null) {
            return new ResponseData<>(HttpStatus.OK, "Register successfully", user);
        } else {
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), "Failed to register");
        }
    }

    @PostMapping("/login")
    public ResponseData<LoginResponseDTO> authenticateUser(@RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException exception) {
            return new ResponseError<>(HttpStatus.UNAUTHORIZED.value(), "Invalid email or password");
        } catch (AuthenticationException exception) {
            return new ResponseError<>(HttpStatus.UNAUTHORIZED.value(), "Authentication failed: " + exception.getMessage());
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtService.generateToken(userDetails.getUsername());

        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse("UNDEFINED");

        LoginResponseDTO response = LoginResponseDTO.builder()
                .username(userDetails.getUsername())
                .jwt(jwt)
                .role(role)
                .build();

        return new ResponseData<LoginResponseDTO>(HttpStatus.OK, "Successfully", response);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user")
    public String userEndpoint(){
        return "Hello, User!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public String adminEndpoint(){
        return "Hello, Admin!";
    }

}
