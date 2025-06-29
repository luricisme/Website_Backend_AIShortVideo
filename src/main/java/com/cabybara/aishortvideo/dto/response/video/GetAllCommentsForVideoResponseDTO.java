package com.cabybara.aishortvideo.dto.response.video;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class GetAllCommentsForVideoResponseDTO {
    private Long id;

    private String avatar;

    private String username;

    private String content;

    private Date createdAt;

    public GetAllCommentsForVideoResponseDTO(Long id, String avatar, String username, String content, Date createdAt) {
        this.id = id;
        this.avatar = avatar;
        this.username = username;
        this.content = content;
        this.createdAt = createdAt;
    }
}
