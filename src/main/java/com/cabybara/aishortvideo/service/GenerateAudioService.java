package com.cabybara.aishortvideo.service;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateAudioRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateAudioResponseDTO;

public interface GenerateAudioService {
    public GenerateAudioResponseDTO generateAudio(GenerateAudioRequestDTO generateAudioRequestDTO);
}
