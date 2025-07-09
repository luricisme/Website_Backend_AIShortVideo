package com.cabybara.aishortvideo.dto.auth.tiktok;

import lombok.Data;

@Data
public class TiktokUserDTO {
    private String open_id;
    private String avatar_large_url;
    private String bio_description;
    private String profile_deep_link;
}