package com.cabybara.aishortvideo.service.video.implement;

import com.cabybara.aishortvideo.dto.response.PageResponse;
import com.cabybara.aishortvideo.dto.response.video.GetAllVideoResponseDTO;
import com.cabybara.aishortvideo.exception.ResourceNotFoundException;
import com.cabybara.aishortvideo.mapper.VideoMapper;
import com.cabybara.aishortvideo.model.*;
import com.cabybara.aishortvideo.repository.DislikedVideoRepository;
import com.cabybara.aishortvideo.repository.LikedVideoRepository;
import com.cabybara.aishortvideo.repository.UserRepository;
import com.cabybara.aishortvideo.repository.VideoRepository;
import com.cabybara.aishortvideo.service.video.VideoService;
import jakarta.transaction.Transactional;
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
    private final VideoMapper videoMapper;

    @Override
    public PageResponse<?> getAllVideosWithRandom() {
        List<Video> results = videoRepository.findAllRandom();

        List<GetAllVideoResponseDTO> videoDTOs = results.stream()
                .map(videoMapper::toDto)
                .toList();

        return PageResponse.builder()
                .totalElements(videoDTOs.size())
                .items(videoDTOs)
                .build();
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

    @Transactional
    @Override
    public void undislikeVideo(Long videoId, Long userId) {
        DislikedVideoId id = new DislikedVideoId(userId, videoId);
        boolean exists = dislikedVideoRepository.existsById(id);
        log.info("Record exists before delete: {}", exists);

        log.info("Attempting to delete with ID: userId={}, videoId={}", userId, videoId);

        dislikedVideoRepository.deleteById(id);

        log.info("Record exists after delete: {}", dislikedVideoRepository.existsById(id));
    }

    private Video getVideoById(Long videoId) {
        return videoRepository.findById(videoId).orElseThrow(() -> new ResourceNotFoundException("Video not found"));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Video not found"));
    }
}
