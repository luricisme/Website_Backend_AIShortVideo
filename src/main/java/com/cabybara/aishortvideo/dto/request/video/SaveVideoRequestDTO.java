package com.cabybara.aishortvideo.dto.request.video;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Getter
@Setter
public class SaveVideoRequestDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String category;
    @NotBlank
    private String style;
    @NotBlank
    private String target;
    @NotBlank
    private String script;
    @NotBlank
    private String audioUrl;
    @NotBlank
    private String videoUrl;
    @NotNull
    private double length;
    @NotNull
    private Long userId;
    private List<String> tags;
    private List<String> imageUrls;
}
