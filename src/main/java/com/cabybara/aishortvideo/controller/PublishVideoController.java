package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.service.youtube.YoutubeApiService;
import com.google.api.services.youtube.model.Video;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("publish")
@Slf4j
public class PublishVideoController {
    private final YoutubeApiService youtubeApiService;

    public PublishVideoController(
            YoutubeApiService youtubeApiService
    ) {
        this.youtubeApiService = youtubeApiService;
    }

    @PostMapping("/youtube/upload_file")
    public ResponseEntity<ResponseData<String>> upload(@RequestParam("file") MultipartFile file,
                                                      @RequestParam String title,
                                                      @RequestParam String description,
                                                      @RequestParam Long userId) throws Exception {
        File temp = File.createTempFile("video-", ".mp4");
        file.transferTo(temp);

        Video uploaded = youtubeApiService.uploadVideo(userId, temp, title, description);

        Boolean isViewable = youtubeApiService.waitUntilVideoIsProcessed(userId, uploaded.getId());

        if (isViewable) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseData<>(HttpStatus.OK, "Successfully", "https://www.youtube.com/watch?v=" + uploaded.getId()));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError<>(HttpStatus.INTERNAL_SERVER_ERROR, "Error when uploading video"));
        }
    }

    @PostMapping("/youtube/upload_url")
    public ResponseEntity<ResponseData<String>> uploadVideoFromUrl(@RequestParam("videoUrl") String videoUrl,
                                      @RequestParam String title,
                                      @RequestParam String description,
                                      @RequestParam Long userId) throws Exception {

        Video uploaded = youtubeApiService.uploadVideoFromUrl(userId, videoUrl, title, description);

        Boolean isViewable = youtubeApiService.waitUntilVideoIsProcessed(userId, uploaded.getId());

        if (isViewable) {
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(new ResponseData<>(HttpStatus.OK, "Successfully", "https://www.youtube.com/watch?v=" + uploaded.getId()));
        } else {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ResponseError<>(HttpStatus.INTERNAL_SERVER_ERROR, "Error when uploading video"));
        }
    }

    @GetMapping("/youtube/statistic")
    public ResponseEntity<ResponseData<Video>> getStatistic(
            @RequestParam("userId") Long userId,
            @RequestParam("videoId") String videoId
    ) throws Exception {
        Video updatedVideo = youtubeApiService.getVideoStatistics(userId, videoId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseData<>(HttpStatus.OK, "Successfully", updatedVideo));
    }
}
