package com.cabybara.aishortvideo.model;

import com.cabybara.aishortvideo.model.composite_id.UserFollowerId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_followers")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserFollower {
    @EmbeddedId
    private UserFollowerId userFollowerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("followerId")
    @JoinColumn(name = "id_follower")
    private User followerUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "id_user")
    private User followingUser;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
}
