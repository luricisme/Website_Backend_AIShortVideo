package com.cabybara.aishortvideo.service.youtube.implement;

import com.cabybara.aishortvideo.model.PublishedVideo;
import com.cabybara.aishortvideo.repository.PublishedVideoRepository;
import com.cabybara.aishortvideo.service.youtube.PublishedVideoService;
import org.springframework.stereotype.Service;

@Service
public class PublishedVideoServiceImpl implements PublishedVideoService {
    private final PublishedVideoRepository publishedVideoRepository;

    public PublishedVideoServiceImpl(
            PublishedVideoRepository publishedVideoRepository
    ) {
        this.publishedVideoRepository = publishedVideoRepository;
    }

    @Override
    public PublishedVideo savePublishedVideo(PublishedVideo publishedVideo) {
        return publishedVideoRepository.save(publishedVideo);
    }
}
