package com.cabybara.aishortvideo.service.cloud.implement;

import com.cabybara.aishortvideo.exception.UploadFileException;
import com.cabybara.aishortvideo.service.cloud.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
public class CloudinaryServiceImpl implements CloudinaryService {
    @Autowired
    private Cloudinary cloudinary;

    private final String TEMP_AVATAR_FOLDER = "Website_AIShortVideoEditor/avatar_temp";
    private final String TEMP_PHOTO_FOLDER = "Website_AIShortVideoEditor/photos_temp";
    private final String TEMP_AUDIO_FOLDER = "Website_AIShortVideoEditor/audio_temp";

    private final String OFFICIAL_PHOTO_FOLDER = "Website_AIShortVideoEditor/photos_official";
    private final String OFFICIAL_AUDIO_FOLDER = "Website_AIShortVideoEditor/audio_official";

    private final String VIDEO_FOLDER = "Website_AIShortVideoEditor/videos";

    @Override
    public String uploadMultipartFile(MultipartFile file, String fileName, String type) throws IOException {
        log.info("Upload multipart files");
        String folder = "";
        switch (type) {
            case "image", "raw" -> {
                folder = TEMP_PHOTO_FOLDER;
            }
            case "video" -> {
                folder = VIDEO_FOLDER;
            }
            case "avatar" -> {
                type = "image";
                folder = TEMP_AVATAR_FOLDER;
            }
            default -> {
                log.warn("Unsupported file type provided: {}", type);
                folder = "undefined";
            }
        }

//        if("image".equals(type)){
//            folder = TEMP_PHOTO_FOLDER;
//        } else if("raw".equals(type)){
//            folder = TEMP_AUDIO_FOLDER;
//        } else if("video".equals(type)){
//            folder = VIDEO_FOLDER;
//        }
        Map<?, ?> result;

        try {
            result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "public_id", fileName,
                            "resource_type", type,
                            "folder", folder,
                            "overwrite", true,
                            "invalidate", true
                    )
            );
        } catch (RuntimeException e) {
            throw new UploadFileException(e.getMessage());
        }
        return result.get("secure_url").toString();
    }

    @Override
    public String uploadBase64File(String base64, String fileName, String type, String mimeType) throws IOException {
        //  image/png
        //  audio/mpeg
        //	video/mp4
        log.info("Upload base64File");
        String folder = "";
        if ("image".equals(type)) {
            folder = TEMP_PHOTO_FOLDER;
        } else if ("raw".equals(type)) {
            folder = TEMP_AUDIO_FOLDER;
        } else if ("video".equals(type)) {
            folder = VIDEO_FOLDER;
        }

//        System.out.println("FOLDER: " + tempFolder + "/" + fileName);

        base64 = "data:" + mimeType + ";base64," + base64;

        Map<?, ?> result = cloudinary.uploader().upload(
                base64,
                ObjectUtils.asMap(
                        "public_id", fileName,
                        "resource_type", type,
                        "folder", folder,
                        "overwrite", true,
                        "invalidate", true
                )
        );
        System.out.println("UPLOAD RESULT RESOURCE TYPE: " + result.get("resource_type"));
        System.out.println("UPLOAD RESULT PUBLIC ID: " + result.get("public_id"));
//        System.out.println("PUBLIC ID: " + result.get("public_id"));
        return result.get("secure_url").toString();
    }

    @Override
    public String moveFileTo(String oldUrl, String type) throws IOException {
        String tempFolder = "";
        if ("image".equals(type)) {
            tempFolder = TEMP_PHOTO_FOLDER;
        } else if ("raw".equals(type)) {
            tempFolder = TEMP_AUDIO_FOLDER;
        }

        String officialFolder = "";
        if ("image".equals(type)) {
            officialFolder = OFFICIAL_PHOTO_FOLDER;
        } else if ("raw".equals(type)) {
            officialFolder = OFFICIAL_AUDIO_FOLDER;
        }

        // Extract file name from url
        String fileNameWithExt = oldUrl.substring(oldUrl.lastIndexOf("/") + 1);
        String fileName = fileNameWithExt.contains(".")
                ? fileNameWithExt.substring(0, fileNameWithExt.lastIndexOf('.'))
                : fileNameWithExt;

        if("raw".equals("raw")){
            fileName = fileName + ".mp3";
        }

        Map<?, ?> result = cloudinary.uploader().upload(
                oldUrl,
                ObjectUtils.asMap(
                        "public_id", fileName,
                        "folder", officialFolder,
                        "overwrite", true,
                        "resource_type", type
                )
        );

        // Delete temp file
        try {
            log.info("Delete temp image");
            String oldPublicId = tempFolder + "/" + fileName;

            cloudinary.uploader().destroy(
                    oldPublicId,
                    ObjectUtils.asMap("resource_type", type)
            );
        } catch (Exception e) {
            log.warn("Can not delete temp file: {}", e.getMessage());
        }
        return result.get("secure_url").toString();
    }

    @Override
    public void deleteFile(String publicId, String type) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                "resource_type", type
        ));
    }
}
