package com.cabybara.aishortvideo.dto.request.create_video;

import com.cabybara.aishortvideo.dto.validator.EnumPattern;
import com.cabybara.aishortvideo.utils.Language;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateScriptRequestDTO {
    @NotBlank
    private String data;

    @NotBlank
    private String style;

    @NotBlank
    private String audience;

    @EnumPattern(name = "lang", regexp = "VIETNAMESE|ENGLISH")
    private Language lang;
}
