package com.cabybara.aishortvideo.dto.request.create_video;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateScriptRequestDTO {
    private String data;
    private String style;
    private String audience;
    private String lang;
}
