package com.cabybara.aishortvideo.service.create_video.implement;

import com.cabybara.aishortvideo.dto.response.create_video.SaveFileResponseDTO;
import com.cabybara.aishortvideo.service.cloud.CloudinaryService;
import com.cabybara.aishortvideo.service.create_video.SaveFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class SaveFileServiceImpl implements SaveFileService {
    private final CloudinaryService cloudinaryService;

    @Override
    public SaveFileResponseDTO saveFile(MultipartFile file, String type) throws IOException {
        String fileURL = "";
        String filename = "";
        if ("audio".equals(type)) {
            filename = "user_audio" + UUID.randomUUID() + ".mp3";
            fileURL = cloudinaryService.uploadMultipartFile(file, filename,"raw");
        } else if ("video".equals(type)) {
            filename = "ai_video" + UUID.randomUUID();
            fileURL = cloudinaryService.uploadMultipartFile(file, filename,"video");
        }
        return SaveFileResponseDTO.builder()
                .fileURL(fileURL)
                .build();
    }

    @Override
    public String uploadAvatar(MultipartFile file, Long userId, String type) throws IOException {
        String filename = "avatar_" + userId;

        return cloudinaryService.uploadMultipartFile(file, filename, "avatar");
    }
}
