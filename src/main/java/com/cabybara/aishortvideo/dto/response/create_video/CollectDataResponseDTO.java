package com.cabybara.aishortvideo.dto.response.create_video;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class CollectDataResponseDTO {
    private String source;
    private String lang;
    private String text;
}
