package com.cabybara.aishortvideo.service.video.implement;

import com.cabybara.aishortvideo.dto.response.PageResponse;
import com.cabybara.aishortvideo.dto.response.video.GetAllVideoResponseDTO;
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
        List<Object[]> results = videoRepository.findAllRandom();

        List<GetAllVideoResponseDTO> videoDTOs = results.stream()
                .map(row -> {
                    Video video = (Video) row[0];
                    Long commentCount = (Long) row[1];
                    int count = commentCount.intValue();
                    GetAllVideoResponseDTO dto = videoMapper.toDto(video);
                    dto.setCommentCnt(count);  // Gán riêng vào DTO trả về
                    return dto;
                })
                .toList();

        return PageResponse.builder()
                .totalElements(videoDTOs.size())
                .items(videoDTOs)
                .build();
    }
}
