package com.cabybara.aishortvideo.dto.response.video;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopTrendingCategoryResponseDTO {
    private String category;
    private Long totalViews;
}
