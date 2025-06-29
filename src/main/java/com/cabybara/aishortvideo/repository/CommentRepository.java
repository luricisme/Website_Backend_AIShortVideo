package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.dto.response.video.CountForVideoResponseDTO;
import com.cabybara.aishortvideo.dto.response.video.GetAllCommentsForVideoResponseDTO;
import com.cabybara.aishortvideo.dto.response.video.GetAllVideoResponseDTO;
import com.cabybara.aishortvideo.model.CommentedVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<CommentedVideo, Long> {
    @Query("""
                SELECT new com.cabybara.aishortvideo.dto.response.video.GetAllCommentsForVideoResponseDTO(
                    c.id,
                    u.id,
                    u.avatar,
                    u.username,
                    c.content,
                    c.createdAt
                )
                FROM CommentedVideo c
                JOIN c.user u
                WHERE c.video.id = :videoId
                ORDER BY c.createdAt DESC
            """)
    List<GetAllCommentsForVideoResponseDTO> getAllComments(@Param("videoId") Long videoId);
}
