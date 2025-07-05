package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.auth.*;
import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.exception.ErrorResponse;
import com.cabybara.aishortvideo.model.UserDetail;
import com.cabybara.aishortvideo.service.auth.GoogleOauthService;
import com.cabybara.aishortvideo.service.auth.JwtService;
import com.cabybara.aishortvideo.service.auth.TiktokOauthService;
import com.cabybara.aishortvideo.service.auth.implement.GoogleOauthServiceImpl;
import com.cabybara.aishortvideo.service.auth.implement.JwtServiceImpl;
import com.cabybara.aishortvideo.service.user.UserService;
import com.cabybara.aishortvideo.service.user.implement.UserServiceImpl;
import com.google.api.client.auth.oauth2.BearerToken;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@Validated
@Tag(name = "Auth Apis")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GoogleOauthService googleOauthService;
    private final TiktokOauthService tiktokOauthService;

    public AuthController(
            UserService userService,
            JwtService jwtService,
            AuthenticationManager authenticationManager,
            GoogleOauthService googleOauthService,
            TiktokOauthService tiktokOauthService
    ) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.googleOauthService = googleOauthService;
        this.tiktokOauthService = tiktokOauthService;
    }

    @PostMapping("/register")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Register successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))
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
    public ResponseEntity<ResponseData<RegisterResponseDTO>> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        RegisterResponseDTO user = userService.addUser(registerRequestDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Register successfully", user));
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
    public ResponseEntity<ResponseData<LoginResponseDTO>> authenticateUser(
            @RequestBody LoginRequestDTO loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException exception) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseError<>(HttpStatus.UNAUTHORIZED.value(), "Invalid email or password"));
        } catch (AuthenticationException exception) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseError<>(HttpStatus.UNAUTHORIZED.value(), "Authentication failed: " + exception.getMessage()));
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetail userDetail = (UserDetail) authentication.getPrincipal();

        String jwt = jwtService.generateToken(userDetail.getUsername());

        String role = userDetail.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst().orElse("UNDEFINED");

        LoginResponseDTO response = LoginResponseDTO.builder()
                .username(userDetail.getUsername())
                .jwt(jwt)
                .role(role)
                .id(userDetail.getId())
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<LoginResponseDTO>(HttpStatus.OK, "Successfully", response));
    }

    @PostMapping("/logout")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseData.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Invalid token",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseError.class))
            )
    })
    public ResponseEntity<ResponseData<String>> logout(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            long now = System.currentTimeMillis();
            long expirationTime = jwtService.extractExpiration(token).getTime();
            long ttl = (expirationTime - now) / 1000;
            jwtService.backlistToken(token, ttl);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseData<>(HttpStatus.OK, "Successfully", null));
        }
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ResponseError(HttpStatus.BAD_REQUEST, "Invalid token"));
    }

    @PostMapping("/oauth/google")
    public ResponseEntity<ResponseData<LoginResponseDTO>> loginWithGoogle(@RequestBody SocialAccountRegisterDTO socialAccountRegisterDTO) {
        LoginResponseDTO loginResponseDTO = googleOauthService.authenticateWithGoogle(socialAccountRegisterDTO.getAuthorizeCode());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", loginResponseDTO));
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/oauth/tiktok")
    public ResponseEntity<ResponseData<Boolean>> loginWithTiktok(
            @RequestBody SocialAccountRegisterDTO socialAccountRegisterDTO,
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        Boolean isAuthenticated = tiktokOauthService.authenticateWithTiktok(socialAccountRegisterDTO.getAuthorizeCode(), userDetail.getId());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", null));
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
