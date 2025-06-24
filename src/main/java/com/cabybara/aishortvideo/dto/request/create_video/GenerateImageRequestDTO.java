package com.cabybara.aishortvideo.dto.request.create_video;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateImageRequestDTO {
    @NotBlank
    private String script;
}
