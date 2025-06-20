package com.cabybara.aishortvideo.service.implement;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateImageRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateImageResponseDTO;
import com.cabybara.aishortvideo.service.GenerateImageService;
import com.cabybara.aishortvideo.service.ai.AIGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateImageServiceImpl implements GenerateImageService {
    private final AIGateway aiGateway;

    @Value("${model.image}")
    private String modelImage;

    @Override
    public GenerateImageResponseDTO generateImage(GenerateImageRequestDTO request) {
        String prompt = createImagePrompt(request);
        List<String> images = aiGateway.generateThreeImagesAsync(prompt);
        saveBase64ImageToFile(images, "D:\\Downloads\\AI-Images");
        return GenerateImageResponseDTO.builder()
                .modelUsed(modelImage)
                .images(images)
                .build();
    }

    private String createImagePrompt(GenerateImageRequestDTO request) {
        String script = request.getScript().trim();
        // Gợi ý prompt ngắn gọn
        return String.format(
                "Create an image about %s", script
        );
    }

    // Test
    public void saveBase64ImageToFile(List<String> base64Images, String outputDir) {
        for (int i = 0; i < base64Images.size(); i++) {
            String base64 = base64Images.get(i);
            try {
                byte[] imageBytes = Base64.getDecoder().decode(base64);
                String fileName = String.format("image_%d.png", i + 1);
                String filePath = outputDir + File.separator + fileName;

                try (OutputStream stream = new FileOutputStream(filePath)) {
                    stream.write(imageBytes);
                    System.out.println("Image saved at: " + filePath);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error while saving image " + (i + 1) + ": " + e.getMessage(), e);
            }
        }
    }
}
