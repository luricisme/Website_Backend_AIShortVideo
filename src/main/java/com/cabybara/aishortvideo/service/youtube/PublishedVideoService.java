package com.cabybara.aishortvideo.service.youtube;

import com.cabybara.aishortvideo.model.PublishedVideo;
import com.google.api.services.youtube.model.Video;

public interface PublishedVideoService {
    PublishedVideo savePublishedVideo(PublishedVideo publishedVideo);

    PublishedVideo updatePublishedVideo(String videoId, Video video);
}
