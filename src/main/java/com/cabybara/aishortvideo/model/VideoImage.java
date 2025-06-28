package com.cabybara.aishortvideo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "video_images")
@Builder
public class VideoImage {
    @EmbeddedId
    private VideoImageId id;

    // Quan hệ ManyToOne với Video
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    @MapsId("videoId") // ánh xạ videoId trong VideoImageId
    @JoinColumn(name = "id_video")
    private Video video;
}
