package com.cabybara.aishortvideo.service.create_video;

import com.cabybara.aishortvideo.dto.response.create_video.SaveFileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface SaveFileService {
    public SaveFileResponseDTO saveFile(MultipartFile file, String type) throws IOException;
}
