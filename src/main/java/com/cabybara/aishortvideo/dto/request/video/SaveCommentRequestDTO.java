package com.cabybara.aishortvideo.dto.request.video;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaveCommentRequestDTO {
    @NotNull
    private Long videoId;

    @NotNull
    private Long userId;

    @NotBlank
    private String content;
}
