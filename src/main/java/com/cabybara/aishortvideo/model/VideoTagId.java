package com.cabybara.aishortvideo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VideoTagId implements Serializable {
    @Column(name = "id_video")
    private Long videoId;

    @Column(name = "tag_name")
    private String tagName;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VideoTagId that)) return false;
        return Objects.equals(videoId, that.videoId) &&
                Objects.equals(tagName, that.tagName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(videoId, tagName);
    }
}
