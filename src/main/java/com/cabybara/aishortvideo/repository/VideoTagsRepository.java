package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.dto.response.video.TopPopularTagResponseDTO;
import com.cabybara.aishortvideo.model.VideoTag;
import com.cabybara.aishortvideo.model.composite_id.VideoTagId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoTagsRepository extends JpaRepository<VideoTag, VideoTagId> {
    @Query("SELECT new com.cabybara.aishortvideo.dto.response.video.TopPopularTagResponseDTO(v.id.tagName, COUNT(v.id.videoId)) " +
            "FROM VideoTag v " +
            "GROUP BY v.id.tagName " +
            "ORDER BY COUNT(v.id.videoId) DESC")
    List<TopPopularTagResponseDTO> findTop5TagsByVideoCount(Pageable pageable);

    @Query(value = """
                SELECT COUNT(DISTINCT vt.id.tagName) 
                FROM VideoTag AS vt
            """)
    long countCreatedTag();
}
