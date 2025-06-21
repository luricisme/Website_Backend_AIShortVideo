package com.cabybara.aishortvideo.service.create_video.implement;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateAudioRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateAudioResponseDTO;
import com.cabybara.aishortvideo.service.cloud.CloudinaryService;
import com.cabybara.aishortvideo.service.create_video.GenerateAudioService;
import com.cabybara.aishortvideo.service.ai.AIGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateAudioServiceImpl implements GenerateAudioService {
    private final AIGateway aiGateway;

    private final CloudinaryService cloudinaryService;

    @Override
    public GenerateAudioResponseDTO generateAudio(GenerateAudioRequestDTO request) throws IOException {
        String base64Audio = aiGateway.callAudioModelAI(request);
//        saveBase64AudioToFile(audio, "D:\\Downloads\\AI-Audio");
        String fileName = "ai_audio_" + UUID.randomUUID() + ".mp3";
        String uploadAudio = cloudinaryService.uploadBase64File(base64Audio, fileName, "raw","audio/mpeg");
        return GenerateAudioResponseDTO.builder()
                .modelUsed("azure-tts-api")
                .voiceType(request.getVoiceType())
                .audio(uploadAudio)
                .build();
    }

    // Test
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
