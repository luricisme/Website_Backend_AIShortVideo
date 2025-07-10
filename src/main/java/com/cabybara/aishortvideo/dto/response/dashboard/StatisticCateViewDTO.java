package com.cabybara.aishortvideo.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatisticCateViewDTO {
    private String cateName;
    private Long viewCount;
}
