package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("""
                SELECT v, COUNT(c.user.id)
                FROM Video v
                LEFT JOIN CommentedVideo c ON c.video.id = v.id
                WHERE v.status = 'PUBLISHED'
                GROUP BY v
                ORDER BY function('RANDOM')
            """)
    List<Object[]> findAllRandom();
}
