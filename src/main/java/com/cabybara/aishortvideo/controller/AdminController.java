package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.auth.RegisterAdminDTO;
import com.cabybara.aishortvideo.dto.auth.RegisterResponseDTO;
import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.user.AdminUserGrowthDTO;
import com.cabybara.aishortvideo.dto.user.AdminUsersOverviewDTO;
import com.cabybara.aishortvideo.exception.ErrorResponse;
import com.cabybara.aishortvideo.service.user.UserService;
import com.cabybara.aishortvideo.utils.UserStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
@Validated
@Tag(name = "Admin Apis")
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("users")
    public ResponseEntity<?> getAllUsers(
            @RequestParam(value = "name", defaultValue = "") String name,
            @Parameter(schema = @Schema(
                    allowableValues = {"ALL", "ACTIVE", "INACTIVE", "PENDING", "DELETED"} // List them manually
            ),
                    example = "ALL")
            @RequestParam(value = "status", defaultValue = "") String status,
            @Parameter(description = "Property to sort by. Allowed values: id, firstName, lastName, email, status, createdAt, updatedAt",
                    schema = @Schema(allowableValues = {"id", "firstName", "lastName", "email", "status", "createdAt", "updatedAt"}),
                    example = "createdAt")
            @RequestParam(value = "sort_criteria", defaultValue = "id") String sortProperty,
            @RequestParam(value = "sort_direction", defaultValue = "asc") String sortDirection,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "5") int pageSize
    ) {
        PageResponseDetail<Object> responseBody = userService.getAllUser(name, status, sortProperty, sortDirection, page, pageSize);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", responseBody));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("users/growth")
    @Operation(summary = "Get total user and growth percent with period_type is ['day', 'week', 'month'] and num")
    public ResponseEntity<?> getUserTotal(
            @RequestParam("period_type") String periodType,
            @RequestParam("num_period") Long numPeriod
    ) {
        AdminUserGrowthDTO responseBody = userService.getGrowthMetrics(periodType, numPeriod);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", responseBody));
    }

    @GetMapping("users/overview")
    public ResponseEntity<ResponseData<AdminUsersOverviewDTO>> getUsers() {

        AdminUsersOverviewDTO userOverviews = userService.getOverviewUsers();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", userOverviews));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("users/{id}")
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

    @PostMapping("register")
    public ResponseEntity<?> registerAdmin(@RequestBody @Validated RegisterAdminDTO registerAdminDTO) {
        RegisterResponseDTO user = userService.addAdminUser(registerAdminDTO);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Register successfully", user));
    }
}

