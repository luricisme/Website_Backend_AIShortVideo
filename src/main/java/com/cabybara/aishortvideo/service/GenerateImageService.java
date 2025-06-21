package com.cabybara.aishortvideo.service;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateImageRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateImageResponseDTO;

public interface GenerateImageService {
    public GenerateImageResponseDTO generateImage(GenerateImageRequestDTO request);
}
