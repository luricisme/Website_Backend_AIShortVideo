package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.dashboard.OverviewDTO;
import com.cabybara.aishortvideo.dto.response.dashboard.StatisticPublishVideoDTO;
import com.cabybara.aishortvideo.dto.response.dashboard.ViewCountByPlatformDTO;
import com.cabybara.aishortvideo.model.UserDetail;
import com.cabybara.aishortvideo.service.dashboard.DashboardService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("dashboard")
@Tag(name = "Dashboard apis")
public class DashboardController {
    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("overview")
    public ResponseEntity<ResponseData<OverviewDTO>> getOverview(
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        OverviewDTO overviewDTO = dashboardService.getOverview(userDetail.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", overviewDTO));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("top_interacted")
    public ResponseEntity<ResponseData<PageResponseDetail<?>>> topInteractedVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        PageResponseDetail<?> responseDetail = dashboardService.topInteractedVideos(userDetail.getId(), page, pageSize);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", responseDetail));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("view_statistic")
    public ResponseEntity<ResponseData<ViewCountByPlatformDTO>> viewStatistic(
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        ViewCountByPlatformDTO response = dashboardService.getViewStatistic(userDetail.getId());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", response));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("platform_statistic")
    public ResponseEntity<ResponseData<StatisticPublishVideoDTO>> platformStatistic(
            @Parameter(description = "google or tiktok",
                    required = true,
                    example = "google")
            @RequestParam("platform") String platform,
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        StatisticPublishVideoDTO statisticPublishVideoDTO = dashboardService.getStatisticPublishVideo(userDetail.getId(), platform);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", statisticPublishVideoDTO));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("view_by_cate")
    public ResponseEntity<ResponseData<PageResponseDetail<Object>>> countViewByCate(
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize,
            @AuthenticationPrincipal UserDetail userDetail
    ) {
        PageResponseDetail<Object> responseDetail = dashboardService.countViewByCate(userDetail.getId(), page, pageSize);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", responseDetail));
    }
}
