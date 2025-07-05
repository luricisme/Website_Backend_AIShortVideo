package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.model.PublishedVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PublishedVideoRepository extends JpaRepository<PublishedVideo, Long> {
    Optional<PublishedVideo> findByVideoId(String id);
}
