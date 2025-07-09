package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.LikedVideo;
import com.cabybara.aishortvideo.model.composite_id.LikedVideoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikedVideoRepository extends JpaRepository<LikedVideo, LikedVideoId> {
}
