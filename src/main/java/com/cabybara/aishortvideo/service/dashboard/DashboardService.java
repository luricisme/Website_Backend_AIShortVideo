package com.cabybara.aishortvideo.service.dashboard;

import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
import com.cabybara.aishortvideo.dto.response.dashboard.OverviewDTO;
import com.cabybara.aishortvideo.dto.response.dashboard.StatisticPublishVideoDTO;
import com.cabybara.aishortvideo.dto.response.dashboard.ViewCountByPlatformDTO;

public interface DashboardService {
    OverviewDTO getOverview(Long userId);

    PageResponseDetail<Object> topInteractedVideos(Long userId, int page, int
            pageSize);

    ViewCountByPlatformDTO getViewStatistic(Long userId);

    StatisticPublishVideoDTO getStatisticPublishVideo(Long userId, String platform);

    PageResponseDetail<Object> countViewByCate(Long userId, int page, int pageSize);
}
