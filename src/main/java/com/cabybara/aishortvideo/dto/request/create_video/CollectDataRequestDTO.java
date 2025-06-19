package com.cabybara.aishortvideo.dto.request.create_video;

import com.cabybara.aishortvideo.utils.DataSource;
import com.cabybara.aishortvideo.utils.Language;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectDataRequestDTO {
    // TODO: Add validation and setup enum validation
    private String query;
    private DataSource source;
    private Language lang;
}
