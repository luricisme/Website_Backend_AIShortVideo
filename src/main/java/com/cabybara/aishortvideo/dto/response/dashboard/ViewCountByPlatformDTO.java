package com.cabybara.aishortvideo.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViewCountByPlatformDTO {
    private Long totalView;
    private Long youtubeView;
    private Long tiktokView;
    private Long mainView;
}
