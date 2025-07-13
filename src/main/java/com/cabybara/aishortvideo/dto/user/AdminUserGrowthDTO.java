package com.cabybara.aishortvideo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserGrowthDTO {
    private Long totalUser;
    private LocalDateTime periodStart;
    private LocalDateTime periodEnd;
    private Long previousUserCount;
    private Long followingUserCount;
    private double growthPercent;
}
