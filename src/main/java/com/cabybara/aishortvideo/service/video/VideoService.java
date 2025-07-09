package com.cabybara.aishortvideo.service.video;

import com.cabybara.aishortvideo.dto.request.video.SaveCommentRequestDTO;
import com.cabybara.aishortvideo.dto.request.video.SaveVideoRequestDTO;
import com.cabybara.aishortvideo.dto.request.video.UpdateCommentRequestDTO;
import com.cabybara.aishortvideo.dto.request.video.UpdateTitleRequestDTO;
import com.cabybara.aishortvideo.dto.response.PageResponse;
import com.cabybara.aishortvideo.dto.response.PageResponseDetail;
import com.cabybara.aishortvideo.dto.response.video.*;

import java.util.List;

public interface VideoService {
    // CREATE VIDEO
    public void saveVideo(SaveVideoRequestDTO request);

    // HOME PAGE
    public PageResponse<?> getAllVideosWithRandom();

    public VideoDetailResponseDTO getOneVideo(Long videoId);

    public void likeVideo(Long videoId, Long userId);

    public void unlikeVideo(Long videoId, Long userId);

    public void dislikeVideo(Long videoId, Long userId);

    public void undislikeVideo(Long videoId, Long userId);

    public CheckLikeStatusResponseDTO checkLikeStatus(Long videoId, Long userId);

    public CountForVideoResponseDTO countForVideo(Long videoId);

    public GetAllCommentsForVideoResponseDTO saveComment(SaveCommentRequestDTO request);

    public PageResponse<?> getAllComments(Long videoId);

    public void updateComment(Long commentId, UpdateCommentRequestDTO request);

    public void deleteComment(Long commentId);

    public void increaseView(Long videoId);

    // TRENDING PAGE
    public List<TopTrendingCategoryResponseDTO> getTopTrendingCategories();

    public List<TopPopularTagResponseDTO> getTopPopularTags();

    public PageResponseDetail<?> getVideoByCategory(int pageNo, int pageSize, String category);

    public PageResponseDetail<?> getVideoByTagName(int pageNo, int pageSize, String tagName);

    public PageResponseDetail<?> getTrendingMonthVideo(int pageNo, int pageSize);

    public PageResponseDetail<?> searchVideo(int pageNo, int pageSize, String... search);

    // PROFILE PAGE
    public PageResponseDetail<?> getMyVideo(int pageNo, int pageSize, Long userId);

    public PageResponseDetail<?> getMyLikedVideo(int pageNo, int pageSize, Long userId);

    // DASHBOARD PROFILE
    public void updateTitle(Long videoId, UpdateTitleRequestDTO request);

    public void deleteVideo(Long videoId);
}
