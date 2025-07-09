package com.cabybara.aishortvideo.model;

import com.cabybara.aishortvideo.model.composite_id.LikedVideoId;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "liked_videos")
public class LikedVideo {
    @EmbeddedId
    private LikedVideoId id;

    // Quan hệ ManyToOne với User
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId") // ánh xạ userId trong LikedVideoId
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    private User user;

    // Quan hệ ManyToOne với Video
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId") // ánh xạ videoId trong LikedVideoId
    @JoinColumn(name = "id_video", referencedColumnName = "id")
    private Video video;

    @Column(name = "created_at")
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
