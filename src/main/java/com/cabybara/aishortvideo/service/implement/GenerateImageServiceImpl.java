package com.cabybara.aishortvideo.service.implement;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateImageRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateImageResponseDTO;
import com.cabybara.aishortvideo.service.GenerateImageService;
import com.cabybara.aishortvideo.service.ai.AIGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

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
        String image = aiGateway.callImageModelAI(prompt);
        String fileName = "D:\\Downloads\\image_" + System.currentTimeMillis() + ".png";
        saveBase64ImageToFile(image, fileName);
        saveBase64ImageToFile(image, fileName);
        return GenerateImageResponseDTO.builder()
                .modelUsed(modelImage)
                .image(image)
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
    public void saveBase64ImageToFile(String base64Image, String outputPath) {
        try {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            try (OutputStream stream = new FileOutputStream(outputPath)) {
                stream.write(imageBytes);
                System.out.println("Image saved at " + outputPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu ảnh từ base64: " + e.getMessage(), e);
        }
    }
}
