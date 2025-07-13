package com.cabybara.aishortvideo.service.dashboard.implement;

import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
import com.cabybara.aishortvideo.dto.response.dashboard.*;
import com.cabybara.aishortvideo.exception.DashboardException;
import com.cabybara.aishortvideo.exception.YoutubeApiException;
import com.cabybara.aishortvideo.mapper.VideoMapper;
import com.cabybara.aishortvideo.model.Video;
import com.cabybara.aishortvideo.repository.PublishedVideoRepository;
import com.cabybara.aishortvideo.repository.UserFollowerRepository;
import com.cabybara.aishortvideo.repository.VideoRepository;
import com.cabybara.aishortvideo.service.dashboard.DashboardService;
import com.cabybara.aishortvideo.service.publisedvideo.youtube.YoutubeApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DashboardServiceImpl implements DashboardService {
    private static final Logger log = LoggerFactory.getLogger(DashboardServiceImpl.class);
    private final VideoRepository videoRepository;
    private final UserFollowerRepository userFollowerRepository;
    private final PublishedVideoRepository publishedVideoRepository;
    private final VideoMapper videoMapper;
    private final YoutubeApiService youtubeApiService;


    public DashboardServiceImpl(
            VideoRepository videoRepository,
            UserFollowerRepository userFollowerRepository, PublishedVideoRepository publishedVideoRepository, VideoMapper videoMapper, YoutubeApiService youtubeApiService) {
        this.videoRepository = videoRepository;
        this.userFollowerRepository = userFollowerRepository;
        this.publishedVideoRepository = publishedVideoRepository;
        this.videoMapper = videoMapper;
        this.youtubeApiService = youtubeApiService;
    }

    @Override
    public OverviewDTO getOverview(Long userId) {
        try {
            Long totalVideo = videoRepository.countByUserId(userId);
            Long totalView = videoRepository.findByUserId(userId).stream()
                    .mapToLong(Video::getViewCnt)
                    .sum();
            Long totalFollower = userFollowerRepository.countByUserFollowerIdUserId(userId);
            Long totalFollowing = userFollowerRepository.countByUserFollowerIdFollowerId(userId);
            Long viewBestVideo = videoRepository.getViewOfBestVideo(userId);

            return OverviewDTO.builder()
                    .totalVideo(totalVideo)
                    .totalView(totalView)
                    .totalFollower(totalFollower)
                    .totalFollowing(totalFollowing)
                    .viewBestVideo(viewBestVideo)
                    .build();
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new DashboardException("Error when getting overview, " + e.getMessage());
        }
    }

    @Override
    public PageResponseDetail<Object> topInteractedVideos(Long userId, int page, int pageSize) {
        if (page > 0) {
            page = page - 1;
        }
        Pageable pageable = PageRequest.of(page, pageSize);
        Page<Video> topInteractedVideos = videoRepository.findTop5ByMostInteractions(userId, pageable);

        List<Video> listInteractedVideos = topInteractedVideos.getContent();
        List<VideoInteractDTO> videoInteractDTOList = listInteractedVideos.stream()
                .map(videoMapper::toVideoInteractDTO)
                .toList();

        return PageResponseDetail.builder()
                .pageNo(page)
                .pageSize(pageSize)
                .totalElements(topInteractedVideos.getTotalElements())
                .totalPage(topInteractedVideos.getTotalPages())
                .items(videoInteractDTOList)
                .build();
    }

    @Override
    public ViewCountByPlatformDTO getViewStatistic(Long userId) {
        try {
            youtubeApiService.fetchVideoStatistics(userId);
        } catch (Exception e) {
            throw new YoutubeApiException("Error when fetching statistic, " + e.getMessage());
        }

        long mainView = videoRepository.getTotalViewCountByUserId(userId);
        Long youtubeViewRaw = publishedVideoRepository.getTotalViewCountByPlatformAndUploadedBy("google", userId);
        long youtubeView = Optional.ofNullable(youtubeViewRaw).orElse(0L);

        Long tiktokViewRaw = publishedVideoRepository.getTotalViewCountByPlatformAndUploadedBy("tiktok", userId);
        long tiktokView = Optional.ofNullable(tiktokViewRaw).orElse(0L);

        long totalView = mainView + youtubeView + tiktokView;

        return ViewCountByPlatformDTO.builder()
                .totalView(totalView)
                .youtubeView(youtubeView)
                .tiktokView(tiktokView)
                .mainView(mainView)
                .build();
    }

    @Override
    public StatisticPublishVideoDTO getStatisticPublishVideo(Long userId, String platform) {
        try {
            youtubeApiService.fetchVideoStatistics(userId);
        } catch (Exception e) {
            throw new YoutubeApiException("Error when fetching statistic, " + e.getMessage());
        }

        long totalView = Optional.ofNullable(publishedVideoRepository.sumViewCountByPlatformAndUploadBy(platform, userId))
                .orElse(0L);
        long totalLike = Optional.ofNullable(publishedVideoRepository.sumLikeCountByPlatformAndUploadBy(platform, userId))
                .orElse(0L);
        long totalComment = Optional.ofNullable(publishedVideoRepository.sumCommentCountByPlatformAndUploadBy(platform, userId))
                .orElse(0L);
        long totalDislike = Optional.ofNullable(publishedVideoRepository.sumDislikeCountByPlatformAndUploadBy(platform, userId))
                .orElse(0L);
        double interactionPercent = 0.0;

        if (totalView > 0) {
            interactionPercent = (double) (totalLike + totalDislike + totalComment) / totalView;
        }

        return StatisticPublishVideoDTO.builder()
                .viewCount(totalView)
                .likeCount(totalLike)
                .dislikeCount(totalDislike)
                .commentCount(totalComment)
                .interactionPercent(interactionPercent)
                .build();
    }

    @Override
    public PageResponseDetail<Object> countViewByCate(Long userId, int page, int pageSize) {
        if (page > 0) {
            page = page - 1;
        }

        Pageable pageable = PageRequest.of(page, pageSize);
        Page<StatisticCateViewDTO> responsePages = videoRepository.countViewsByCategoryForUser(userId, pageable);

        return PageResponseDetail.builder()
                .pageNo(page)
                .pageSize(pageSize)
                .totalPage(responsePages.getTotalPages())
                .totalElements(responsePages.getTotalElements())
                .items(responsePages.getContent())
                .build();
    }


}
