package com.cabybara.aishortvideo.dto.response.video;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAllTagResponseDTO {
    private String tagName;
    private Long videoCnt;
}
