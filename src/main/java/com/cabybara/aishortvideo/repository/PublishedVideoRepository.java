package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.dto.response.dashboard.StatisticPublishVideoDTO;
import com.cabybara.aishortvideo.model.PublishedVideo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublishedVideoRepository extends JpaRepository<PublishedVideo, Long> {
    Optional<PublishedVideo> findByVideoId(String id);

    @Query("""
        SELECT SUM(pv.viewCount)
        FROM PublishedVideo pv
        WHERE pv.platform LIKE :platform
    """)
    Long getTotalViewCountByPlatform(String platform);


    @Query("SELECT SUM(pv.viewCount) FROM PublishedVideo pv WHERE pv.platform = :platform AND pv.uploadedBy = :userId")
    Long sumViewCountByPlatformAndUploadBy(String platform, Long userId);

    @Query("SELECT SUM(pv.likeCount) FROM PublishedVideo pv WHERE pv.platform = :platform AND pv.uploadedBy = :userId")
    Long sumLikeCountByPlatformAndUploadBy(String platform, Long userId);

    @Query("SELECT SUM(pv.commentCount) FROM PublishedVideo pv WHERE pv.platform = :platform AND pv.uploadedBy = :userId")
    Long sumCommentCountByPlatformAndUploadBy(String platform, Long userId);

    @Query("SELECT SUM(pv.dislikeCount) FROM PublishedVideo pv WHERE pv.platform = :platform AND pv.uploadedBy = :userId")
    Long sumDislikeCountByPlatformAndUploadBy(String platform, Long userId);

    @Query("""
        SELECT pv.videoId FROM PublishedVideo pv WHERE pv.uploadedBy = :userId
    """)
    List<String> findAllVideoIdByUpdatedBy(Long userId);
}
