package com.cabybara.aishortvideo.model;

import com.cabybara.aishortvideo.utils.UserRole;
import com.cabybara.aishortvideo.utils.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String email;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "role")
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private UserStatus status = UserStatus.ACTIVE;

    private String username;
    private String password;
    private String bio;
    private String avatar;
    private String facebook;
    private String instagram;
    private String tiktok;
    private String youtube;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
