package com.cabybara.aishortvideo.service.create_video;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateAudioRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateAudioResponseDTO;

import java.io.IOException;

public interface GenerateAudioService {
    public GenerateAudioResponseDTO generateAudio(GenerateAudioRequestDTO generateAudioRequestDTO) throws IOException;
}
