package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.request.create_video.CollectDataRequestDTO;
import com.cabybara.aishortvideo.dto.request.create_video.GenerateAudioRequestDTO;
import com.cabybara.aishortvideo.dto.request.create_video.GenerateImageRequestDTO;
import com.cabybara.aishortvideo.dto.request.create_video.GenerateScriptRequestDTO;
import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.dto.response.create_video.CollectDataResponseDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateAudioResponseDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateImageResponseDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateScriptResponseDTO;
import com.cabybara.aishortvideo.service.CollectDataService;
import com.cabybara.aishortvideo.service.GenerateAudioService;
import com.cabybara.aishortvideo.service.GenerateImageService;
import com.cabybara.aishortvideo.service.GenerateScriptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/create-video")
@Validated
@Slf4j
@Tag(name = "Create Video Controller")
@RequiredArgsConstructor
public class CreateVideoController {
    private final CollectDataService collectDataService;
    private final GenerateScriptService generateScriptService;
    private final GenerateImageService generateImageService;
    private final GenerateAudioService generateAudioService;

    private static final String ERROR_MESSAGE = "errorMessage={}";

    @Operation(method = "POST", summary = "Collect data from sources", description = "Collect data from sources (wikipedia, wikidata, ai) in language (vi, en)")
    @PostMapping(value = "collect-data")
    public ResponseData<CollectDataResponseDTO> collectData(@Valid @RequestBody CollectDataRequestDTO request) {
        log.info("Collect data from {} with query {}", request.getSource(), request.getQuery());
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Collect data", collectDataService.collectData(request));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), "Collect data fail");
        }
    }

    @Operation(method = "POST", summary = "Generate script from data", description = "Generate script from data that you have collected")
    @PostMapping(value = "generate-script")
    public ResponseData<GenerateScriptResponseDTO> generateScript(@Valid @RequestBody GenerateScriptRequestDTO request) {
        log.info("Generate script with data {}", request.getData());
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Generate script", generateScriptService.generateScript(request));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), "Generate script fail");
        }
    }

    @Operation(method = "POST", summary = "Generate image from script", description = "Generate image from script that you have created")
    @PostMapping(value = "generate-image")
    public ResponseData<GenerateImageResponseDTO> generateImage(@Valid @RequestBody GenerateImageRequestDTO request){
        log.info("Generate image with script {}", request.getScript());
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Generate image", generateImageService.generateImage(request));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), "Generate image fail");
        }
    }

    @Operation(method = "POST", summary = "Generate audio from script", description = "Generate audio from script that you have created")
    @PostMapping(value = "generate-audio")
    public ResponseData<GenerateAudioResponseDTO> generateAudio(@Valid @RequestBody GenerateAudioRequestDTO request){
        log.info("Generate audio with script {}", request.getScript());
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Generate audio", generateAudioService.generateAudio(request));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), "Generate audio fail");
        }
    }
}
