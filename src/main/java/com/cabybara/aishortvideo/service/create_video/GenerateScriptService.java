package com.cabybara.aishortvideo.service.create_video;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateScriptRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateScriptResponseDTO;

public interface GenerateScriptService {
    public GenerateScriptResponseDTO generateScript(GenerateScriptRequestDTO request);
}
