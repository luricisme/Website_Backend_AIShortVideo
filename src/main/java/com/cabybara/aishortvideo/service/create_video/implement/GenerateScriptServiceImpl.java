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
    private final int MAX_LENGTH = 50;


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
                "Write or develop a coherent story based on the data '%s' with the following requirements:\n" +
                        "- Style: %s\n" +
                        "- Audience: %s\n" +
                        "- Language: %s\n" +
                        "- Format:\n" +
                        "  + Plain prose (no scenes, no titles)\n" +
                        "  + Third-person narration\n" +
                        "- Length: Maximum %d words\n" +
                        "- The output must be written entirely in the specified language above, with no other languages mixed in\n" +
                        "IMPORTANT: Return only the story content without formatting",
                request.getData(),
                request.getStyle(),
                request.getAudience(),
                request.getLang().name(),
                MAX_LENGTH
        );
    }
}
