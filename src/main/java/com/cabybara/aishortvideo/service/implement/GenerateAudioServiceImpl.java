package com.cabybara.aishortvideo.service.implement;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateAudioRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateAudioResponseDTO;
import com.cabybara.aishortvideo.service.GenerateAudioService;
import com.cabybara.aishortvideo.service.ai.AIGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateAudioServiceImpl implements GenerateAudioService {
    private final AIGateway aiGateway;

    @Override
    public GenerateAudioResponseDTO generateAudio(GenerateAudioRequestDTO request) {
        String audio = aiGateway.callAudioModelAI(request);
        saveBase64AudioToFile(audio, "D:\\Downloads\\AI-Audio");
        return GenerateAudioResponseDTO.builder()
                .modelUsed("azure-tts-api")
                .voiceType(request.getVoiceType())
                .audio(audio)
                .build();
    }

    public void saveBase64AudioToFile(String base64Audio, String outputDir) {
        try {
            byte[] audioBytes = Base64.getDecoder().decode(base64Audio);
            String fileName = "audio_" + UUID.randomUUID().toString() + ".mp3";
            String filePath = outputDir + File.separator + fileName;

            try (OutputStream stream = new FileOutputStream(filePath)) {
                stream.write(audioBytes);
                System.out.println("Audio saved at: " + filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while saving audio : " + e.getMessage(), e);
        }
    }
}
