package com.cabybara.aishortvideo.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewDTO {
    private Long totalVideo;
    private Long totalView;
    private Long totalFollower;
    private Long totalFollowing;
    private Long viewBestVideo;
}
