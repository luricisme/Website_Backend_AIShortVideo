package com.cabybara.aishortvideo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video_tags")
@Builder
public class VideoTag {
    @EmbeddedId
    private VideoTagId id;

    // Quan hệ ManyToOne với Video
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("videoId") // ánh xạ videoId trong VideoTagId
    @JoinColumn(name = "id_video")
    private Video video;
}
