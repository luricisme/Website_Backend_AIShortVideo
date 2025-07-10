package com.cabybara.aishortvideo.service.dashboard;

import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
import com.cabybara.aishortvideo.dto.response.dashboard.OverviewDTO;
import com.cabybara.aishortvideo.dto.response.dashboard.ViewCountByPlatformDTO;
import com.cabybara.aishortvideo.model.Video;

import java.util.List;

public interface DashboardService {
    OverviewDTO getOverview(Long userId);

    PageResponseDetail<Object> topInteractedVideos(Long userId, int page, int
            pageSize);

    ViewCountByPlatformDTO getViewStatistic(Long userId);
}
