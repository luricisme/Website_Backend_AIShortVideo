package com.cabybara.aishortvideo.dto.response.video;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CheckLikeStatusResponseDTO {
    private boolean liked;
    private boolean disliked;
}
