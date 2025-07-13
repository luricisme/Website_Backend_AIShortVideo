package com.cabybara.aishortvideo.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StatisticPublishVideoDTO {
    private Long viewCount;
    private Long commentCount;
    private Long likeCount;
    private Long dislikeCount;
    private Double interactionPercent;
}
