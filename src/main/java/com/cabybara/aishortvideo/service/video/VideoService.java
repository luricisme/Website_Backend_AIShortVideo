package com.cabybara.aishortvideo.service.video;

import com.cabybara.aishortvideo.dto.response.PageResponse;
import com.cabybara.aishortvideo.dto.response.video.CheckLikeStatusResponseDTO;

public interface VideoService {
    public PageResponse<?> getAllVideosWithRandom();

    public void likeVideo(Long videoId, Long userId);

    public void unlikeVideo(Long videoId, Long userId);

    public void dislikeVideo(Long videoId, Long userId);

    public void undislikeVideo(Long videoId, Long userId);

    public CheckLikeStatusResponseDTO checkLikeStatus(Long videoId, Long userId);
}
