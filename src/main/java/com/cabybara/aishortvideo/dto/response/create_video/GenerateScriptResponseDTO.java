package com.cabybara.aishortvideo.dto.response.create_video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GenerateScriptResponseDTO {
    private String modelUsed;
    private String script;
}
