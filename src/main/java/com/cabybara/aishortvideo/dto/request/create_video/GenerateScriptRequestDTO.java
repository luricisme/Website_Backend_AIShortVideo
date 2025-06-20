package com.cabybara.aishortvideo.dto.request.create_video;

import com.cabybara.aishortvideo.dto.validator.EnumPattern;
import com.cabybara.aishortvideo.utils.Language;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateScriptRequestDTO {
    private String data;

    private String style;

    private String audience;

    @EnumPattern(name = "lang", regexp = "VIETNAMESE|ENGLISH")
    private Language lang;
}
