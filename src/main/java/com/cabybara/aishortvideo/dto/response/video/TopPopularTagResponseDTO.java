package com.cabybara.aishortvideo.dto.response.video;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopPopularTagResponseDTO {
    private String tagName;
    private Long videoCnt;
}
