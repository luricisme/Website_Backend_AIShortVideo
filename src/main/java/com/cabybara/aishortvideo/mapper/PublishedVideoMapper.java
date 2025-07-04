package com.cabybara.aishortvideo.mapper;

import com.cabybara.aishortvideo.model.PublishedVideo;
import com.google.api.client.util.DateTime;
import com.google.api.services.youtube.model.Video;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = "spring")
public interface PublishedVideoMapper {
    PublishedVideoMapper INSTANCE = Mappers.getMapper(PublishedVideoMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "id", target = "videoId")
    @Mapping(source = "snippet.title", target = "title")
    @Mapping(source = "snippet.description", target = "description")
    @Mapping(source = "snippet.publishedAt", target = "publishedAt")
    @Mapping(source = "snippet.channelId", target = "channelId")
    @Mapping(source = "snippet.channelTitle", target = "channelTitle")
    @Mapping(source = "snippet.thumbnails.high.url", target = "thumbnailUrl")
    @Mapping(source = "statistics.viewCount", target = "viewCount", defaultValue = "0L")
    @Mapping(source = "statistics.likeCount", target = "likeCount", defaultValue = "0L")
    @Mapping(source = "statistics.dislikeCount", target = "dislikeCount", defaultValue = "0L")
    @Mapping(source = "statistics.commentCount", target = "commentCount", defaultValue = "0L")
    @Mapping(target = "uploadedBy", ignore = true)
    @Mapping(target = "uploadDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastUpdated", expression = "java(java.time.LocalDateTime.now())")
    PublishedVideo toPublishedVideoFromYoutubeVideo(Video video);

    default LocalDateTime mapPublishedAt(DateTime publishedAt) {
        if (publishedAt == null) {
            return null;
        }

        return LocalDateTime.ofInstant(Instant.ofEpochMilli(publishedAt.getValue()), ZoneOffset.UTC);
    }
}
