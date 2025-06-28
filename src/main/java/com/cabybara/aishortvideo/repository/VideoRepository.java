package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    @Query("""
                SELECT v
                FROM Video v
                WHERE v.status = 'PUBLISHED'
                ORDER BY function('RANDOM')
            """)
    List<Video> findAllRandom();
}
