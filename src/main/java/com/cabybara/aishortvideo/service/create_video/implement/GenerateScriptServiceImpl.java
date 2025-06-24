package com.cabybara.aishortvideo.service.create_video.implement;

import com.cabybara.aishortvideo.dto.request.create_video.GenerateScriptRequestDTO;
import com.cabybara.aishortvideo.dto.response.create_video.GenerateScriptResponseDTO;
import com.cabybara.aishortvideo.service.create_video.GenerateScriptService;
import com.cabybara.aishortvideo.service.ai.AIGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenerateScriptServiceImpl implements GenerateScriptService {
    private final AIGateway aiGateway;

    @Value("${model.chat}")
    private String modelChat;
    private final int MAX_LENGTH = 80;


    @Override
    public GenerateScriptResponseDTO generateScript(GenerateScriptRequestDTO request) {
        System.out.println("Lang in use: " + request.getLang().name());
        String prompt = createScriptPrompt(request);
        String script = aiGateway.callChatModelAI(prompt);
        return GenerateScriptResponseDTO.builder()
                .modelUsed(modelChat)
                .script(script)
                .build();
    }

    private String createScriptPrompt(GenerateScriptRequestDTO request) {
        return String.format(
                "Hãy viết hoặc phát triển một câu chuyện liền mạch dựa vào dữ liệu '%s' với các yêu cầu:\n" +
                        "- Phong cách: %s\n" +
                        "- Đối tượng: %s\n" +
                        "- Được viết ở ngôn ngữ: %s\n" +
                        "- Định dạng:\n" +
                        "  + Văn xuôi thuần túy (không phân cảnh, không tiêu đề)\n" +
                        "  + Ngôi kể thứ ba\n" +
                        "- Độ dài: Tối đa %d từ\n" +  // thêm dòng này
                        "QUAN TRỌNG: Chỉ trả về nội dung câu chuyện không định dạng",
                request.getData(),
                request.getStyle(),
                request.getAudience(),
                request.getLang().name(),
                MAX_LENGTH
        );
    }
}
