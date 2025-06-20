package com.cabybara.aishortvideo.dto.response.create_video;

import com.cabybara.aishortvideo.utils.DataSource;
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
