package com.cabybara.aishortvideo.dto.response.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VideoInteractDTO {
    private String title;
    private Long totalInteraction;
    private String videoUrl;
    private String thumbnailUrl;
}
