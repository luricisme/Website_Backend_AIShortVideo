package com.cabybara.aishortvideo.service.implement;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateAudioRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateAudioResponseDTO;
import com.cabybara.aishortvideo.service.GenerateAudioService;
import com.cabybara.aishortvideo.service.ai.AIGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateAudioServiceImpl implements GenerateAudioService {
    private final AIGateway aiGateway;

    @Override
    public GenerateAudioResponseDTO generateAudio(GenerateAudioRequestDTO generateAudioRequestDTO) {
        String audio = aiGateway.callAudioModelAI(generateAudioRequestDTO);
        return GenerateAudioResponseDTO.builder()
                .modelUsed("google-tts-api")
                .audio(audio)
                .build();
    }
}
