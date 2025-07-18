package com.cabybara.aishortvideo.model;

import com.cabybara.aishortvideo.model.composite_id.VideoImageId;
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
    @MapsId("videoId") // ánh xạ videoId trong VideoImageId
    @JoinColumn(name = "id_video")
    private Video video;
}
