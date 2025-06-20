package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.auth.AuthRequestDTO;
import com.cabybara.aishortvideo.dto.auth.AuthResponseDTO;
import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.service.implement.JwtServiceImpl;
import com.cabybara.aishortvideo.service.implement.UserServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @PostMapping("/login")
    public ResponseData<AuthResponseDTO> authenticateUser(@RequestBody AuthRequestDTO loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException exception) {
            return new ResponseError<>(HttpStatus.NOT_FOUND.value(), "Bad credentials");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        String jwt = jwtService.generateToken(userDetails.getUsername());

        String role = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .toString();

        AuthResponseDTO response = AuthResponseDTO.builder()
                .username(userDetails.getUsername())
                .jwt(jwt)
                .role(role)
                .build();

        return new ResponseData<AuthResponseDTO>(HttpStatus.OK, "Successfully", response);
    }
}
