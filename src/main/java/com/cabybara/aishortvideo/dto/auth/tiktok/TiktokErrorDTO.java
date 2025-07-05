package com.cabybara.aishortvideo.dto.auth.tiktok;

import lombok.Data;

@Data
public class TiktokErrorDTO {
    private String code;
    private String message;
    private String log_id;
}
