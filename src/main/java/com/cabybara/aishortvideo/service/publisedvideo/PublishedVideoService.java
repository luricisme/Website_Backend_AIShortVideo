package com.cabybara.aishortvideo.service.publisedvideo;

import com.cabybara.aishortvideo.model.PublishedVideo;
import com.google.api.services.youtube.model.Video;

public interface PublishedVideoService {
    PublishedVideo savePublishedVideo(PublishedVideo publishedVideo);

    PublishedVideo updatePublishedVideo(String videoId, Video video);
}
