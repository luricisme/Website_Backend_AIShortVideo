package com.cabybara.aishortvideo.dto.response.video;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class CountForVideoResponseDTO {
    private int likeCnt;
    private int dislikeCnt;
    private int commentCnt;

    public CountForVideoResponseDTO(int likeCnt, int dislikeCnt, int commentCnt) {
        this.likeCnt = likeCnt;
        this.dislikeCnt = dislikeCnt;
        this.commentCnt = commentCnt;
    }
}
