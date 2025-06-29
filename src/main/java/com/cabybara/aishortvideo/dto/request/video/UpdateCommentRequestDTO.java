package com.cabybara.aishortvideo.dto.request.video;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UpdateCommentRequestDTO {
    @NotBlank
    private String content;
}
