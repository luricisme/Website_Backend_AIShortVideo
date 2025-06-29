package com.cabybara.aishortvideo.service.video.implement;

import com.cabybara.aishortvideo.dto.request.video.SaveCommentRequestDTO;
import com.cabybara.aishortvideo.dto.request.video.UpdateCommentRequestDTO;
import com.cabybara.aishortvideo.dto.response.PageResponse;
import com.cabybara.aishortvideo.dto.response.video.CheckLikeStatusResponseDTO;
import com.cabybara.aishortvideo.dto.response.video.CountForVideoResponseDTO;
import com.cabybara.aishortvideo.dto.response.video.GetAllCommentsForVideoResponseDTO;
import com.cabybara.aishortvideo.dto.response.video.VideoDetailResponseDTO;
import com.cabybara.aishortvideo.exception.ResourceNotFoundException;
import com.cabybara.aishortvideo.mapper.VideoMapper;
import com.cabybara.aishortvideo.model.*;
import com.cabybara.aishortvideo.repository.*;
import com.cabybara.aishortvideo.service.video.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    private final VideoRepository videoRepository;
    private final LikedVideoRepository likedVideoRepository;
    private final DislikedVideoRepository dislikedVideoRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final VideoMapper videoMapper;

    @Override
    public PageResponse<?> getAllVideosWithRandom() {
        List<Video> results = videoRepository.findAllRandom();

        List<VideoDetailResponseDTO> videoDTOs = results.stream()
                .map(videoMapper::toDto)
                .toList();

        return PageResponse.builder()
                .totalElements(videoDTOs.size())
                .items(videoDTOs)
                .build();
    }

    @Override
    public VideoDetailResponseDTO getOneVideo(Long videoId) {
        Video result = videoRepository.findById(videoId).orElseThrow(() -> new ResourceNotFoundException("Video not found"));
        return videoMapper.toDto(result);
    }

    @Override
    public void likeVideo(Long videoId, Long userId) {
        Video video = getVideoById(videoId);
        User user = getUserById(userId);
        likedVideoRepository.save(LikedVideo.builder()
                .id(LikedVideoId.builder()
                        .videoId(videoId)
                        .userId(userId)
                        .build())
                .video(video)
                .user(user)
                .build());
    }

    @Override
    public void unlikeVideo(Long videoId, Long userId) {
        likedVideoRepository.deleteById(LikedVideoId.builder()
                .videoId(videoId)
                .userId(userId)
                .build());
    }

    @Override
    public void dislikeVideo(Long videoId, Long userId) {
        Video video = getVideoById(videoId);
        User user = getUserById(userId);
        dislikedVideoRepository.save(DislikedVideo.builder()
                .id(DislikedVideoId.builder()
                        .videoId(videoId)
                        .userId(userId)
                        .build())
                .video(video)
                .user(user)
                .build());
    }

    @Override
    public void undislikeVideo(Long videoId, Long userId) {
        DislikedVideoId id = new DislikedVideoId(userId, videoId);
        boolean exists = dislikedVideoRepository.existsById(id);
//        log.info("Record exists before delete: {}", exists);
//        log.info("Attempting to delete with ID: userId={}, videoId={}", userId, videoId);
        dislikedVideoRepository.deleteById(id);
//        log.info("Record exists after delete: {}", dislikedVideoRepository.existsById(id));
    }

    @Override
    public CheckLikeStatusResponseDTO checkLikeStatus(Long videoId, Long userId) {
        boolean liked = likedVideoRepository.existsById(LikedVideoId.builder()
                .userId(userId)
                .videoId(videoId)
                .build());
        boolean disliked = dislikedVideoRepository.existsById(DislikedVideoId.builder()
                .userId(userId)
                .videoId(videoId)
                .build());
        return CheckLikeStatusResponseDTO.builder()
                .liked(liked)
                .disliked(disliked)
                .build();
    }

    @Override
    public CountForVideoResponseDTO countForVideo(Long videoId) {
        return videoRepository.getVideoCountBy(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found"));
    }

    @Override
    public Long saveComment(SaveCommentRequestDTO request) {
        User user = getUserById(request.getUserId());
        Video video = getVideoById(request.getVideoId());

        CommentedVideo comment = CommentedVideo.builder()
                .user(user)
                .video(video)
                .content(request.getContent())
                .build();
        commentRepository.save(comment);
        return comment.getId();
    }

    @Override
    public PageResponse<?> getAllComments(Long videoId) {
        List<GetAllCommentsForVideoResponseDTO> comments = commentRepository.getAllComments(videoId);
        return PageResponse.builder()
                .totalElements(comments.size())
                .items(comments)
                .build();
    }

    @Override
    public void updateComment(Long commentId, UpdateCommentRequestDTO request) {
        CommentedVideo comment = getCommentById(commentId);
        comment.setContent(request.getContent());
        commentRepository.save(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    private Video getVideoById(Long videoId) {
        System.out.println("TOI DANG KIEM VIDEO VOI ID = " + videoId);
        return videoRepository.findById(videoId).orElseThrow(() -> new ResourceNotFoundException("Video not found"));
    }

    private User getUserById(Long userId) {
        System.out.println("TOI DANG KIEM USER VOI ID = " + userId);
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CommentedVideo getCommentById(Long commentId) {
        System.out.println("TOI DANG KIEM COMMENT VOI ID = " + commentId);
        return commentRepository.findById(commentId).orElseThrow(() -> new ResourceNotFoundException("Comment not found"));
    }
}
