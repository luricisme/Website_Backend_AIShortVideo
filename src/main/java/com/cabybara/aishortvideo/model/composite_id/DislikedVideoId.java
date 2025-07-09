package com.cabybara.aishortvideo.model.composite_id;

import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DislikedVideoId implements Serializable{
    @Column(name = "id_user")
    private Long userId;

    @Column(name = "id_video")
    private Long videoId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DislikedVideoId that = (DislikedVideoId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(videoId, that.videoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, videoId);
    }
}
