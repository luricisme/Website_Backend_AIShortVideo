package com.cabybara.aishortvideo.dto.response.video;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoTagDTO {
    private Long videoId;
    private String tagName;
}
