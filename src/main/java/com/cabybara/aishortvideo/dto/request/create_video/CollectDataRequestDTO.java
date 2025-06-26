package com.cabybara.aishortvideo.dto.request.create_video;

import com.cabybara.aishortvideo.dto.validator.EnumPattern;
import com.cabybara.aishortvideo.utils.DataSource;
import com.cabybara.aishortvideo.utils.Language;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectDataRequestDTO {
    @NotBlank
    private String query;

    @EnumPattern(name = "source", regexp = "Wikipedia|Wikidata|AI")
    private DataSource source;

    @EnumPattern(name = "lang", regexp = "Vietnamese|English|Chinese")
    private Language lang;
}
