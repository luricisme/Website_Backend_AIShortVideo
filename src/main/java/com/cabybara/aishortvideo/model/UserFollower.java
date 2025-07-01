package com.cabybara.aishortvideo.model;

import com.cabybara.aishortvideo.model.composite_id.UserFollowerId;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_follows")
public class UserFollower {
    @EmbeddedId
    private UserFollowerId userFollowerId;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
