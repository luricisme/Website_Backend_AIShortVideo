package com.cabybara.aishortvideo.service.cloud.implement;

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

    private final String TEMP_PHOTO_FOLDER = "Website_AIShortVideoEditor/photos_temp";
    private final String TEMP_AUDIO_FOLDER = "Website_AIShortVideoEditor/audio_temp";

    private final String OFFICIAL_PHOTO_FOLDER = "Website_AIShortVideoEditor/photos_official";
    private final String OFFICIAL_AUDIO_FOLDER = "Website_AIShortVideoEditor/audio_official";

    @Override
    public String uploadMultipartFile(MultipartFile file, String fileName, String type) throws IOException {
        String tempFolder = "";
        if("image".equals(type)){
            tempFolder = TEMP_PHOTO_FOLDER;
        } else if("raw".equals(type)){
            tempFolder = TEMP_AUDIO_FOLDER;
        }

        Map<?, ?> result = cloudinary.uploader().upload(
                file.getBytes(),
                ObjectUtils.asMap(
                        "public_id", tempFolder + "/" + fileName,
                        "resource_type", type
                )
        );
        return result.get("secure_url").toString();
    }

    @Override
    public String uploadBase64File(String base64, String fileName, String type, String mimeType) throws IOException {
        log.info("Upload base64File");
        String tempFolder = "";
        if("image".equals(type)){
            tempFolder = TEMP_PHOTO_FOLDER;
        } else if("raw".equals(type)){
            tempFolder = TEMP_AUDIO_FOLDER;
        }

//        System.out.println("FOLDER: " + tempFolder + "/" + fileName);

        base64 = "data:" + mimeType + ";base64," + base64;

        Map<?, ?> result = cloudinary.uploader().upload(
                base64,
                ObjectUtils.asMap(
                        "public_id", tempFolder + "/" + fileName,
                        "resource_type", type,
                        "folder", tempFolder
                )
        );
//        System.out.println("PUBLIC ID: " + result.get("public_id"));
        return result.get("secure_url").toString();
    }

    @Override
    public String moveFileTo(String fileName, String targetFolder, String type) throws IOException {
        String tempFolder = "";
        if("image".equals(type)){
            tempFolder = TEMP_PHOTO_FOLDER;
        } else if("raw".equals(type)){
            tempFolder = TEMP_AUDIO_FOLDER;
        }

        String officialFolder = "";
        if("image".equals(type)){
            officialFolder = OFFICIAL_PHOTO_FOLDER;
        } else if("raw".equals(type)){
            officialFolder = OFFICIAL_AUDIO_FOLDER;
        }

        Map<?, ?> result = cloudinary.uploader().rename(
                tempFolder + "/" + fileName,
                targetFolder + "/" + fileName,
                ObjectUtils.asMap(
                        "overwrite", true,
                        "resource_type", type
                )
        );
        return result.get("secure_url").toString();
    }

    @Override
    public void deleteFile(String publicId, String type) throws IOException {
        cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                "resource_type", type
        ));
    }
}
