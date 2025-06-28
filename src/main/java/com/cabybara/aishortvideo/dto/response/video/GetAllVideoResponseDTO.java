package com.cabybara.aishortvideo.dto.response.video;

import com.cabybara.aishortvideo.dto.user.UserDTO;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetAllVideoResponseDTO {
    private Long id;
    private String title;
    private String category;
    private String style;
    private String target;
    private String script;
    private String audioUrl;
    private String videoUrl;
    private int likeCnt;
    private int dislikeCnt;
    private int viewCnt;
    private double length;
    private String thumbnail;
    private String status;

    private UserDTO user;

    private List<VideoTagDTO> tags;
}
