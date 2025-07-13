package com.cabybara.aishortvideo.service.publisedvideo;

import com.cabybara.aishortvideo.exception.VideoNotFoundException;
import com.cabybara.aishortvideo.model.PublishedVideo;
import com.cabybara.aishortvideo.repository.PublishedVideoRepository;
import com.google.api.services.youtube.model.Video;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

    @Override
    public PublishedVideo updatePublishedVideo(String videoId, Video video) {
        PublishedVideo existingPublishedVideo = publishedVideoRepository.findByVideoId(videoId)
                .orElseThrow(() -> new VideoNotFoundException("Video not found"));

        existingPublishedVideo.setViewCount(video.getStatistics().getViewCount().longValue());
        existingPublishedVideo.setLikeCount(video.getStatistics().getLikeCount().longValue());
        existingPublishedVideo.setDislikeCount(video.getStatistics().getDislikeCount().longValue());
        existingPublishedVideo.setCommentCount(video.getStatistics().getCommentCount().longValue());
        existingPublishedVideo.setLastUpdated(LocalDateTime.now());

        return publishedVideoRepository.save(existingPublishedVideo);
    }
}
