package com.cabybara.aishortvideo.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Value("${cloud.cloudinary.name}")
    private String cloudName;
    @Value("${cloud.cloudinary.apiKey}")
    private String apiKey;
    @Value("${cloud.cloudinary.apiSecret}")
    private String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret, // Luôn sử dụng HTTPS (SSL) khi truy cập tài nguyên từ Cloudinary.
                "secure", true
        );
        return new Cloudinary(config);
    }
}
