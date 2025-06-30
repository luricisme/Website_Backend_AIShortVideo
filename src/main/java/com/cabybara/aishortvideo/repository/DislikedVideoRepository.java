package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.DislikedVideo;
import com.cabybara.aishortvideo.model.DislikedVideoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DislikedVideoRepository extends JpaRepository<DislikedVideo, DislikedVideoId> {
}
