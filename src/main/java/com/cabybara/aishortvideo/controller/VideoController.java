package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.dto.response.video.CheckLikeStatusResponseDTO;
import com.cabybara.aishortvideo.service.video.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/video")
@Validated
@Slf4j
@Tag(name = "Video APIs")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    private static final String ERROR_MESSAGE = "errorMessage={}";

    @Operation(method = "GET", summary = "Get all videos randomly", description = "Get all videos randomly for home page")
    @GetMapping(value = "")
    public ResponseData<?> collectData() {
        log.info("Get all videos randomly for home page");
        return new ResponseData<>(HttpStatus.OK.value(), "videos", videoService.getAllVideosWithRandom());
    }

    @Operation(method = "POST", summary = "Like video", description = "Like video (One times)")
    @PostMapping(value = "/like/{videoId}")
    public ResponseData<Void> likeVideo(@PathVariable @Min(1) Long videoId, @RequestParam @Min(1) Long userId) {
        log.info("Like video, videoId={}, userId={}", videoId, userId);
        try {
            videoService.likeVideo(videoId, userId);
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Like video successfully");
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
            return new ResponseData<>(HttpStatus.ACCEPTED.value(), "Dislike video successfully");
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
            return new ResponseData<>(HttpStatus.NO_CONTENT.value(), "Check status like and dislike button successfully", videoService.checkLikeStatus(videoId, userId));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError(HttpStatus.BAD_REQUEST.value(), "Check status like and dislike button fail");
        }
    }
}
