package com.cabybara.aishortvideo.repository;

import com.cabybara.aishortvideo.dto.response.dashboard.StatisticCateViewDTO;
import com.cabybara.aishortvideo.dto.response.video.CountForVideoResponseDTO;
import com.cabybara.aishortvideo.dto.response.video.TopTrendingCategoryResponseDTO;
import com.cabybara.aishortvideo.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
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
    Optional<CountForVideoResponseDTO> getVideoCount(@Param("videoId") Long videoId);

    @Query("SELECT new com.cabybara.aishortvideo.dto.response.video.TopTrendingCategoryResponseDTO(v.category, SUM(v.viewCnt)) " +
            "FROM Video v " +
            "GROUP BY v.category " +
            "ORDER BY SUM(v.viewCnt) DESC")
    List<TopTrendingCategoryResponseDTO> findTop5CategoriesByTotalViews(Pageable pageable);

    @Query("""
                SELECT v
                FROM Video v
                WHERE v.category = :category AND v.status = 'PUBLISHED'
            """)
    Page<Video> findVideoByCategory(@Param("category") String category, Pageable pageable);

    @Query("""
                SELECT v
                FROM Video v
                JOIN v.tags t
                WHERE t.id.tagName = :tagName AND v.status = 'PUBLISHED'
            """)
    Page<Video> findVideoByTagName(@Param("tagName") String tagName, Pageable pageable);

    @Query("""
                SELECT v
                FROM Video v
                WHERE v.status = 'PUBLISHED'
                    AND EXTRACT(MONTH FROM v.createdAt) = EXTRACT(MONTH FROM CURRENT_DATE)
                    AND EXTRACT(YEAR FROM v.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE)
                ORDER BY v.viewCnt DESC, v.createdAt DESC
            """)
    Page<Video> findTrendingMonthVideo(Pageable pageable);

    @Query("""
                SELECT v
                FROM Video v
                JOIN v.user u
                WHERE u.id = :userId
            """)
    Page<Video> findMyVideo(@Param("userId") Long userId, Pageable pageable);

    @Query("""
                SELECT v
                FROM Video v
                JOIN v.likedUsers lu
                WHERE lu.user.id = :userId
            """)
    Page<Video> findMyLikedVideos(@Param("userId") Long userId, Pageable pageable);


    @Query(value = """
                SELECT COUNT(*) 
                FROM Video AS v
                WHERE v.status = 'PUBLISHED'
                  AND EXTRACT(DAY FROM v.createdAt) = EXTRACT(DAY FROM CURRENT_DATE)
                  AND EXTRACT(MONTH FROM v.createdAt) = EXTRACT(MONTH FROM CURRENT_DATE)
                  AND EXTRACT(YEAR FROM v.createdAt) = EXTRACT(YEAR FROM CURRENT_DATE)
            """)
    long countCreatedVideoToday();

    @Query("""
                SELECT v
                FROM Video v
            """)
    Page<Video> findAllVideo(Pageable pageable);

    Long countByUserId(Long userId);

    List<Video> findByUserId(Long userId);

    @Query("""
        SELECT v.viewCnt
        FROM Video v
        WHERE v.user.id = :userId
        ORDER BY CASE WHEN v.viewCnt = 0 THEN 0 ELSE v.likeCnt * 1.0 / v.viewCnt END DESC
        LIMIT 1
    """)
    Long getViewOfBestVideo(Long userId);

    @Query(value = """
        SELECT v
        FROM Video v
        WHERE v.user.id = :userId
        ORDER BY (v.viewCnt + v.likeCnt + v.commentCnt + v.dislikeCnt) DESC
    """)
    Page<Video> findTop5ByMostInteractions(Long userId, Pageable pageable);

    @Query("""
        SELECT SUM(v.viewCnt)
        FROM Video v
        WHERE v.user.id = :userId
    """)
    Long getTotalViewCountByUserId(Long userId);

    @Query("""
        SELECT new com.cabybara.aishortvideo.dto.response.dashboard.StatisticCateViewDTO(v.category, SUM(v.viewCnt))
        FROM Video v
        WHERE v.user.id = :userId
        GROUP BY v.category
        ORDER BY SUM(v.viewCnt) DESC
    """)
    Page<StatisticCateViewDTO> countViewsByCategoryForUser(
            @Param("userId") Long userId,
            Pageable pageable
    );
}
