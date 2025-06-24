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

    @EnumPattern(name = "lang", regexp = "Vietnamese|English|Chinese")
    private Language lang;
    /*
    vi-VN-HoaiMyNeural	    Giọng Bắc, tự nhiên, phù hợp đọc tin tức
    vi-VN-NamMinhNeural	    Giọng Bắc, trầm ấm
    en-US-JennyNeural	    Giọng Mỹ phổ thông, đa dụng
    en-US-DavisNeural	    Giọng trầm, chuyên nghiệp
    zh-CN-XiaoxiaoNeural	Giọng nữ trẻ trung, có thể đa cảm xúc
    zh-CN-YunyangNeural	    Giọng phát thanh viên
     */
    @NotBlank
    private String voiceType;

    @NotBlank
    private String speed;
}
