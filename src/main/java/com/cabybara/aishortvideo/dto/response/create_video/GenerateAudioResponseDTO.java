package com.cabybara.aishortvideo.dto.response.create_video;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class GenerateAudioResponseDTO {
    private String modelUsed;
    private String audio;
}
