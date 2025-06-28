package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
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
}
