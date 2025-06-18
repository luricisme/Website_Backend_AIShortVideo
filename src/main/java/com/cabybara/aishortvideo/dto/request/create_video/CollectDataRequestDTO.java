package com.cabybara.aishortvideo.dto.request.create_video;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectDataRequestDTO {
    private String query;
    private String source;
    private String lang;
}
