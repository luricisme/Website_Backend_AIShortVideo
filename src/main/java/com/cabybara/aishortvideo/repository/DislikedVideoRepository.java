package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.DislikedVideo;
import com.cabybara.aishortvideo.model.composite_id.DislikedVideoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DislikedVideoRepository extends JpaRepository<DislikedVideo, DislikedVideoId> {
}
