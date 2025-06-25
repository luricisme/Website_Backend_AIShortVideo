package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.auth.LoginRequestDTO;
import com.cabybara.aishortvideo.dto.auth.LoginResponseDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterRequestDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterResponseDTO;
import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.exception.ErrorResponse;
import com.cabybara.aishortvideo.service.implement.JwtServiceImpl;
import com.cabybara.aishortvideo.service.implement.UserServiceImpl;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Register successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Registration failed or invalid input",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "User with this email already exists",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public ResponseData<RegisterResponseDTO> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        RegisterResponseDTO user = userService.addUser(registerRequestDTO);
        return new ResponseData<>(HttpStatus.OK, "Register successfully", user);
    }

    @PostMapping("/login")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Login successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid email or password",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))
            )
    })
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
    @ApiResponses(value = {
        @ApiResponse(
                responseCode = "200",
                description = "Successfully",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))
        ),
        @ApiResponse(
                responseCode = "401",
                description = "Unauthorized - Invalid email or password",
                content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    public String userEndpoint(){
        return "Hello, User!";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized - Invalid email or password",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    public String adminEndpoint(){
        return "Hello, Admin!";
    }

}
