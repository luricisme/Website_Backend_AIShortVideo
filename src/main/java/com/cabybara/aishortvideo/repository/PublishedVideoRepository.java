package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.PublishedVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublishedVideoRepository extends JpaRepository<PublishedVideo, Long> {
}
