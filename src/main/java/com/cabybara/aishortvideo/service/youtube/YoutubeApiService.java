package com.cabybara.aishortvideo.service.youtube;

import com.google.api.services.youtube.model.Video;

import java.io.File;
import java.util.List;

public interface YoutubeApiService {
    Video uploadVideo(Long userId, File videoFile, String title, String description) throws Exception;

    Video uploadVideoFromUrl(Long userId, String videoUrl, String title, String description, String privacyStatus) throws Exception;

    Video getVideoStatistics(Long userId, String videoId) throws Exception;

    Boolean waitUntilVideoIsProcessed(Long userId, String videoId) throws Exception;

    List<Video> getUploadedVideos(Long userId, String channelId) throws Exception;
}
