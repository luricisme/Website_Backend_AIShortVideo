package com.cabybara.aishortvideo.dto.request.create_video;

import com.cabybara.aishortvideo.dto.validator.EnumPattern;
import com.cabybara.aishortvideo.utils.Language;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GenerateAudioRequestDTO {
    @NotBlank
    private String script;

    @EnumPattern(name = "lang", regexp = "VIETNAMESE|ENGLISH")
    private Language lang;
    /*
    vi-VN-Wavenet-A	    Giọng nữ mềm mại
    vi-VN-Wavenet-B	    Giọng nam chuẩn
    en-US-Wavenet-F	    Giọng nữ trẻ, rõ ràng
    en-US-Wavenet-D	    Giọng nam mạnh, phổ biến
     */
    @NotBlank
    private String voiceType;

    @NotBlank
    private String speed;
}
