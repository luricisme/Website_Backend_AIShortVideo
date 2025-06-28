package com.cabybara.aishortvideo.service.video.implement;

import com.cabybara.aishortvideo.dto.response.PageResponse;
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

    @Override
    public PageResponse<?> getAllVideosWithRandom() {
        List<Video> videos = videoRepository.findAllRandom();

        return PageResponse.builder()
                .totalElements(videos.size())
                .items(videos)
                .build();
    }
}
