package com.cabybara.aishortvideo.service.video;

import com.cabybara.aishortvideo.dto.request.video.SaveCommentRequestDTO;
import com.cabybara.aishortvideo.dto.request.video.UpdateCommentRequestDTO;
import com.cabybara.aishortvideo.dto.response.PageResponse;
import com.cabybara.aishortvideo.dto.response.video.CheckLikeStatusResponseDTO;
import com.cabybara.aishortvideo.dto.response.video.CountForVideoResponseDTO;

public interface VideoService {
    public PageResponse<?> getAllVideosWithRandom();

    public void likeVideo(Long videoId, Long userId);

    public void unlikeVideo(Long videoId, Long userId);

    public void dislikeVideo(Long videoId, Long userId);

    public void undislikeVideo(Long videoId, Long userId);

    public CheckLikeStatusResponseDTO checkLikeStatus(Long videoId, Long userId);

    public CountForVideoResponseDTO countForVideo(Long videoId);

    public Long saveComment(SaveCommentRequestDTO request);

    public PageResponse<?> getAllComments(Long videoId);

    public void updateComment(Long commentId, UpdateCommentRequestDTO request);
}
