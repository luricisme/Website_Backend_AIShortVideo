package com.cabybara.aishortvideo.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminUsersOverviewDTO {
    private Long numActive;
    private Long numInactive;
    private Long numNewUserToday;
}
