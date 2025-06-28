package com.cabybara.aishortvideo.service.video;

import com.cabybara.aishortvideo.dto.response.PageResponse;

public interface VideoService {
    PageResponse<?> getAllVideosWithRandom();
}
