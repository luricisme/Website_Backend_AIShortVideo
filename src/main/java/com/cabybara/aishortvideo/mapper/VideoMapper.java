package com.cabybara.aishortvideo.mapper;

import com.cabybara.aishortvideo.dto.response.dashboard.VideoInteractDTO;
import com.cabybara.aishortvideo.dto.response.video.VideoDetailResponseDTO;
import com.cabybara.aishortvideo.dto.response.video.VideoTagDTO;
import com.cabybara.aishortvideo.model.Video;
import com.cabybara.aishortvideo.model.VideoTag;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface VideoMapper {
    @Mapping(target = "status", expression = "java(video.getStatus().name())")
    @Mapping(target = "tags", expression = "java(mapTags(video.getTags()))")
    @Mapping(target = "commentCnt", source = "commentCnt")
    VideoDetailResponseDTO toDto(Video video);

    @Mapping(target = "totalInteraction", source = ".", qualifiedByName = "toTotalInteraction")
    VideoInteractDTO toVideoInteractDTO(Video video);

    @Named("toTotalInteraction")
    static Long toTotalInteraction(Video video) {
        return (long) video.getLikeCnt() + video.getDislikeCnt() + video.getViewCnt() + video.getCommentCnt();
    }

    default List<VideoTagDTO> mapTags(List<VideoTag> tags) {
        if (tags == null) return List.of();
        return tags.stream()
                .map(tag -> VideoTagDTO.builder()
                        .videoId(tag.getId().getVideoId())
                        .tagName(tag.getId().getTagName())
                        .build())
                .toList();
    }
}
