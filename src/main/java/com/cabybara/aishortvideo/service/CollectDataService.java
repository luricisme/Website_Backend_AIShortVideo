package com.cabybara.aishortvideo.service;

import com.cabybara.aishortvideo.dto.request.create_video.CollectDataRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.CollectDataResponseDTO;

public interface CollectDataService {
    CollectDataResponseDTO collectData(CollectDataRequestDTO request);
}
