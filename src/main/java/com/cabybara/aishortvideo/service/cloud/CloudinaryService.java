package com.cabybara.aishortvideo.service.cloud;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CloudinaryService {
    public String uploadMultipartFile(MultipartFile file, String fileName, String type) throws IOException;
    public String uploadBase64File(String base64, String fileName, String type, String mimeType) throws IOException;
    public String moveFileTo(String oldUrl, String type) throws IOException;
    public void deleteFile(String publicId, String type) throws IOException;
}
