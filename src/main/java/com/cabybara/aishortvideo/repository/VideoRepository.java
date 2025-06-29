package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.dto.response.video.CountForVideoResponseDTO;
import com.cabybara.aishortvideo.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("""
                SELECT v
                FROM Video v
                WHERE v.status = 'PUBLISHED'
                ORDER BY function('RANDOM')
            """)
    List<Video> findAllRandom();

    @Query("""
                SELECT v
                FROM Video v
                WHERE v.status = 'PUBLISHED' AND v.id = :videoId
            """)
    Video findOne(@Param("videoId") Long videoId);

    @Query("""
                SELECT new com.cabybara.aishortvideo.dto.response.video.CountForVideoResponseDTO(v.likeCnt, v.dislikeCnt, v.commentCnt)
                FROM Video v
                WHERE v.id = :videoId
            """)
    Optional<CountForVideoResponseDTO> getVideoCountBy(@Param("videoId") Long videoId);
}
