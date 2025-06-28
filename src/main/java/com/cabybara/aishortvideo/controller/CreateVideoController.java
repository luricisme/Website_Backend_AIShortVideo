package com.cabybara.aishortvideo.controller;

import com.cabybara.aishortvideo.dto.request.create_video.*;
import com.cabybara.aishortvideo.dto.response.ResponseData;
import com.cabybara.aishortvideo.dto.response.ResponseError;
import com.cabybara.aishortvideo.dto.response.create_video.*;
import com.cabybara.aishortvideo.service.create_video.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/create-video")
@Validated
@Slf4j
@Tag(name = "Create Video APIs")
@RequiredArgsConstructor
public class CreateVideoController {
    private final CollectDataService collectDataService;
    private final GenerateScriptService generateScriptService;
    private final GenerateImageService generateImageService;
    private final GenerateAudioService generateAudioService;
    private final SaveFileService saveFileService;

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
    public ResponseData<GenerateImageResponseDTO> generateImage(@Valid @RequestBody GenerateImageRequestDTO request) {
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
    public ResponseData<GenerateAudioResponseDTO> generateAudio(@Valid @RequestBody GenerateAudioRequestDTO request) {
        log.info("Generate audio with script {}", request.getScript());
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Generate audio", generateAudioService.generateAudio(request));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), "Generate audio fail");
        }
    }

    @Operation(
            method = "POST",
            summary = "Save file from client",
            description = """
                    Save audio or video file from client (Upload or Record).
                    
                    📝 Form-data fields:
                    - file: multipart file to upload (audio/video)
                    - type: string (should be either 'audio' or 'video')
                    
                    📌 Note:
                    - Max file size is limited by server config.
                    - Supported formats: .mp3, .mp4, .wav, .webm, etc.
                    """
    )
    @PostMapping(value = "save-file")
    public ResponseData<SaveFileResponseDTO> saveFile(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) {
        log.info("Save file from client");
        try {
            return new ResponseData<>(HttpStatus.OK.value(), "Save file from client", saveFileService.saveFile(file, type));
        } catch (Exception e) {
            log.error(ERROR_MESSAGE, e.getMessage(), e.getCause());
            return new ResponseError<>(HttpStatus.BAD_REQUEST.value(), "Save file fail");
        }
    }
}
