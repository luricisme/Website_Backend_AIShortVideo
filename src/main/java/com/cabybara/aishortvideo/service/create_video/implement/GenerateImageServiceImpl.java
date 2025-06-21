package com.cabybara.aishortvideo.service.create_video.implement;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateImageRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateImageResponseDTO;
import com.cabybara.aishortvideo.service.cloud.CloudinaryService;
import com.cabybara.aishortvideo.service.create_video.GenerateImageService;
import com.cabybara.aishortvideo.service.ai.AIGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateImageServiceImpl implements GenerateImageService {
    private final AIGateway aiGateway;

    private final CloudinaryService cloudinaryService;

    @Value("${model.image}")
    private String modelImage;

    @Override
    public GenerateImageResponseDTO generateImage(GenerateImageRequestDTO request) {
        String prompt = createImagePrompt(request);
        List<String> base64Images = aiGateway.generateThreeImagesAsync(prompt);
//        saveBase64ImageToFile(images, "D:\\Downloads\\AI-Images");
        List<String> uploadedImageUrls = new ArrayList<>();
        for(int i = 0; i < base64Images.size(); i++) {
            String base64 = base64Images.get(i);
            String filename = "ai_image_" + UUID.randomUUID();
            try {
                String url = cloudinaryService.uploadBase64File(base64, filename, "image","image/png");
                uploadedImageUrls.add(url);
            } catch (IOException e) {
                throw new RuntimeException("Upload image to cloudinary fail", e);
            }
        }

        return GenerateImageResponseDTO.builder()
                .modelUsed(modelImage)
                .images(uploadedImageUrls)
                .build();
    }

    private String createImagePrompt(GenerateImageRequestDTO request) {
        String script = request.getScript().trim();
        return String.format(
                "Create an image related to the passage: %s", script
        );
    }

    // Test
    private void saveBase64ImageToFile(List<String> base64Images, String outputDir) {
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
