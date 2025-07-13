package com.cabybara.aishortvideo.dto.auth.tiktok;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TiktokUserInfoDTO {
    private TiktokUserDataDTO data;
    private TiktokErrorDTO error;
}
