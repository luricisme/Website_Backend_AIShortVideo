package com.cabybara.aishortvideo.service.video.implement;

import com.cabybara.aishortvideo.dto.response.PageResponse;
import com.cabybara.aishortvideo.dto.response.video.GetAllVideoResponseDTO;
import com.cabybara.aishortvideo.exception.ResourceNotFoundException;
import com.cabybara.aishortvideo.mapper.VideoMapper;
import com.cabybara.aishortvideo.model.Video;
import com.cabybara.aishortvideo.repository.VideoRepository;
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
    private final VideoMapper videoMapper;

    @Override
    public PageResponse<?> getAllVideosWithRandom() {
        List<Video> results = videoRepository.findAllRandom();

//        for (Video video : results) {
//            System.out.println("ID: " + video.getId());
//            System.out.println("Title: " + video.getTitle());
//            System.out.println("Like count: " + video.getLikeCnt());
//            System.out.println("Comment count: " + video.getCommentCnt());
//            System.out.println("User: " + (video.getUser() != null ? video.getUser().getUsername() : "null"));
//            System.out.println("----");
//        }

        List<GetAllVideoResponseDTO> videoDTOs = results.stream()
                .map(videoMapper::toDto)
                .toList();

        return PageResponse.builder()
                .totalElements(videoDTOs.size())
                .items(videoDTOs)
                .build();
    }

    private Video getVideoById(long videoId) {
        return videoRepository.findById(videoId).orElseThrow(() -> new ResourceNotFoundException("Video not found"));
    }
}
