package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.request.video.SaveCommentRequestDTO;
import com.cabybara.aishortvideo.dto.request.video.UpdateCommentRequestDTO;
import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.dto.response.video.*;
import com.cabybara.aishortvideo.exception.ResourceNotFoundException;
import com.cabybara.aishortvideo.repository.VideoRepository;
import com.cabybara.aishortvideo.service.video.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/video")
@Validated
@Slf4j
@Tag(name = "Video APIs")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    private static final String ERROR_MESSAGE = "errorMessage={}";

    // HOME PAGE
    @Operation(method = "GET", summary = "Get all videos randomly", description = "Get all videos randomly for home page")
    @GetMapping(value = "")
    public ResponseData<?> collectData() {
        log.info("Get all videos randomly for home page");
        return new ResponseData<>(HttpStatus.OK.value(), "videos", videoService.getAllVideosWithRandom());
    }

    @Operation(summary = "Get video detail", description = "Get video detail by videoId")
    @GetMapping("/{videoId}")
    public ResponseData<VideoDetailResponseDTO> getOneVideo(@PathVariable @Min(1) Long videoId) {
        try {
            log.info("Get video detail, videoId={}", videoId);
            return new ResponseData<>(HttpStatus.OK.value(), "Video detail", videoService.getOneVideo(videoId));
        } catch (ResourceNotFoundException e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @Operation(method = "POST", summary = "Like video", description = "Like video (One times)")
    @PostMapping(value = "/like/{videoId}")
    public ResponseData<Void> likeVideo(@PathVariable @Min(1) Long videoId, @RequestParam @Min(1) Long userId) {
        log.info("Like video, videoId={}, userId={}", videoId, userId);
        try {
            videoService.likeVideo(videoId, userId);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Like video successfully");
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Like video fail");
        }
    }

    @Operation(method = "DELETE", summary = "Unlike video", description = "Unlike video")
    @DeleteMapping(value = "/like/{videoId}")
    public ResponseData<Void> unlikeVideo(@PathVariable @Min(1) Long videoId, @RequestParam @Min(1) Long userId) {
        log.info("Unlike video, videoId={}, userId={}", videoId, userId);
        try {
            videoService.unlikeVideo(videoId, userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Unlike video successfully");
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Unlike video fail");
        }
    }

    @Operation(method = "POST", summary = "Dislike video", description = "Dislike video (One times)")
    @PostMapping(value = "/dislike/{videoId}")
    public ResponseData<Void> dislikeVideo(@PathVariable @Min(1) Long videoId, @RequestParam @Min(1) Long userId) {
        log.info("Dislike video, videoId={}, userId={}", videoId, userId);
        try {
            videoService.dislikeVideo(videoId, userId);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Dislike video successfully");
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Dislike video fail");
        }
    }

    @Operation(method = "DELETE", summary = "Undislike video", description = "Undislike video")
    @DeleteMapping(value = "/dislike/{videoId}")
    public ResponseData<Void> undislikeVideo(@PathVariable @Min(1) Long videoId, @RequestParam @Min(1) Long userId) {
        log.info("Undislike video, videoId={}, userId={}", videoId, userId);
        try {
            videoService.undislikeVideo(videoId, userId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Undislike video successfully");
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Undislike video fail");
        }
    }

    @Operation(method = "GET", summary = "Check like and dislike button status", description = "Check like and dislike button status ")
    @GetMapping(value = "/check-like-dislike-status/{videoId}")
    public ResponseData<CheckLikeStatusResponseDTO> checkLikeStatus(@PathVariable @Min(1) Long videoId, @RequestParam @Min(1) Long userId) {
        log.info("Check like and dislike button status, videoId={}, userId={}", videoId, userId);
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Check status like and dislike button successfully", videoService.checkLikeStatus(videoId, userId));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Check status like and dislike button fail");
        }
    }

    @Operation(method = "GET", summary = "Count like, dislike and comment", description = "Count like, dislike and comment")
    @GetMapping(value = "/count/{videoId}")
    public ResponseData<CountForVideoResponseDTO> countForVideo(@PathVariable @Min(1) Long videoId) {
        log.info("Count like, dislike and comment for video, videoId={}", videoId);
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Count like, dislike and comment successfully", videoService.countForVideo(videoId));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Count like, dislike and comment fail");
        }
    }

    @Operation(method = "GET", summary = "Get all comments", description = "Get all comments for video")
    @GetMapping(value = "/comment/{videoId}")
    public ResponseData<?> getAllComments(@PathVariable @Min(1) Long videoId) {
        log.info("Get all comments randomly for video");
        return new ResponseData<>(HttpStatus.OK.value(), "Get all comments for video", videoService.getAllComments(videoId));
    }

    @Operation(method = "POST", summary = "Comment video", description = "Comment video (Login first)")
    @PostMapping(value = "/comment")
    public ResponseData<GetAllCommentsForVideoResponseDTO> saveComment(@Valid @RequestBody SaveCommentRequestDTO request) {
//        log.info("Comment video, videoId={}, userId={}", request.getVideoId(), request.getUserId());
        try {
            GetAllCommentsForVideoResponseDTO comment = videoService.saveComment(request);
            return new ResponseData<>(HttpStatus.CREATED.value(), "Comment video successfully", comment);
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Comment video fail");
        }
    }

    @Operation(method = "PATCH", summary = "Update comment", description = "Update comment")
    @PatchMapping(value = "/comment/{commentId}")
    public ResponseData<Void> updateComment(@PathVariable @Min(1) Long commentId, @Valid @RequestBody UpdateCommentRequestDTO request) {
        log.info("Update comment, commentId={}", commentId);
        try {
            videoService.updateComment(commentId, request);
            return new ResponseData<>(HttpStatus.OK.value(), "Update comment successfully");
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Update comment fail");
        }
    }

    @Operation(summary = "Delete comment", description = "Delete comment permanently")
    @DeleteMapping("/comment/{commendId}")
    public ResponseData<Void> deleteComment(@Min(value = 1) @PathVariable Long commendId) {
        log.info("Delete comment, commentId={}", commendId);
        try {
            videoService.deleteComment(commendId);
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Delete comment successfully");
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Delete comment fail");
        }
    }

    @Operation(method = "PATCH", summary = "Increase view", description = "Increase view of video")
    @PatchMapping(value = "/view/{videoId}")
    public ResponseData<Void> increaseView(@PathVariable @Min(1) Long videoId) {
        log.info("Increase view of video, videoId={}", videoId);
        try {
            videoService.increaseView(videoId);
            return new ResponseData<>(HttpStatus.OK.value(), "Increase view of video successfully");
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Increase view of video fail");
        }
    }

    // TRENDING PAGE
    @Operation(method = "GET", summary = "Get top 5 trending categories", description = "Get top 5 trending categories based on views")
    @GetMapping(value = "/top-trending-categories")
    public ResponseData<List<TopTrendingCategoryResponseDTO>> getTopTrendingCategories() {
        log.info("Get top 5 trending categories");
        return new ResponseData<>(HttpStatus.OK.value(), "Get top 5 trending categories", videoService.getTopTrendingCategories());
    }

    @Operation(method = "GET", summary = "Get top 5 popular tags", description = "Get top 5 popular tags based on number of videos")
    @GetMapping(value = "/top-popular-tags")
    public ResponseData<List<TopPopularTagResponseDTO>> getTopPopularTags() {
        log.info("Get top 5 popular tags");
        return new ResponseData<>(HttpStatus.OK.value(), "Get top 5 popular tags", videoService.getTopPopularTags());
    }

    @Operation(method = "GET", summary = "Get videos by category", description = "Get videos by category")
    @GetMapping(value = "/category/{category}")
    public ResponseData<?> getAllVideosByCategory(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(1) @RequestParam(defaultValue = "10", required = false) int pageSize,
            @PathVariable String category) {
        log.info("Get videos by category");
        return new ResponseData<>(HttpStatus.OK.value(), "Get videos by category", videoService.getVideoByCategory(pageNo, pageSize, category));
    }

    @Operation(method = "GET", summary = "Get videos by tag name", description = "Get videos by tag name")
    @GetMapping(value = "/tag/{tagName}")
    public ResponseData<?> getAllVideosByTagName(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(1) @RequestParam(defaultValue = "10", required = false) int pageSize,
            @PathVariable String tagName) {
        log.info("Get videos by tag name");
        return new ResponseData<>(HttpStatus.OK.value(), "Get videos by tag name", videoService.getVideoByTagName(pageNo, pageSize, tagName));
    }

    @Operation(method = "GET", summary = "Get trending video in this month", description = "Get trending video in this month")
    @GetMapping(value = "/trending-month")
    public ResponseData<?> getTrendingMonthVideo(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(1) @RequestParam(defaultValue = "10", required = false) int pageSize) {
        log.info("Get trending video in this month");
        return new ResponseData<>(HttpStatus.OK.value(), "Get trending video in this month", videoService.getTrendingMonthVideo(pageNo, pageSize));
    }

    @Operation(method = "GET", summary = "Search video", description = "Search video by title, category, style, target and script")
    @GetMapping(value = "/search")
    public ResponseData<?> searchVideo(
            @RequestParam(defaultValue = "0", required = false) int pageNo,
            @Min(1) @RequestParam(defaultValue = "10", required = false) int pageSize,
            @RequestParam(required = false) String... search) {
        log.info("Search video");
        return new ResponseData<>(HttpStatus.OK.value(), "Search video", videoService.searchVideo(pageNo, pageSize, search));
    }
}
