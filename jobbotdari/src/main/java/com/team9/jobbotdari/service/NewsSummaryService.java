package com.team9.jobbotdari.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.jobbotdari.config.GroqApiConfig;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NewsSummaryService {
    private final GroqApiConfig groqApiConfig;

    // RestClient 인스턴스를 생성하여 HTTP 요청을 수행합니다.
    private final RestClient restClient = RestClient.create();

    // JSON 파싱을 위한 Jackson ObjectMapper 인스턴스
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 사용자가 전달한 뉴스 콘텐츠(newsContent)를 포함하여 Groq API에 요청을 보낸 후,
     * 응답 JSON에서 choices 배열의 첫 번째 요소의 message.content 값을 추출하여,
     * 그 결과를 "description" 키로 담은 Map 객체로 반환합니다.
     *
     * 요청 과정:
     *
     * 1. 시스템 프롬프트 템플릿에 newsContent를 삽입하여 최종 메시지를 생성
     * 2. HTTP 헤더를 구성하여 JSON 콘텐츠 타입과 Bearer 인증 헤더를 설정
     * 3. 사용자 메시지 객체를 생성 (role: user, content: 최종 메시지)
     * 4. API 요청 본문에 모델명, 메시지 리스트, 온도 등의 설정값을 포함
     * 5. RestClient를 사용해 Groq API에 POST 요청을 보내고, 응답 JSON 문자열을 수신
     * 6. ObjectMapper를 사용하여 응답 JSON을 파싱하고, choices 배열의 첫 번째 요소에서 message.content 추출
     * 7. 추출한 content를 "description" 키로 Map에 담아 반환
     *
     *
     * @param newsContent 사용자 입력 뉴스 콘텐츠 (예: 기사 링크 또는 기사 요약)
     * @return Groq API 응답에서 추출한 요약 내용을 "description" 키로 담은 Map 객체
     */

    public Map<String, Object> getNewsSummary(String newsContent) {
        // 1. 시스템 프롬프트 템플릿에 newsContent를 삽입하여 최종 메시지(message)를 생성합니다.
        String message = String.format(groqApiConfig.getSystemPromptTemplate(), newsContent);

        // 2. HTTP 요청 헤더를 구성합니다.
        //    - Content-Type: JSON 형식
        //    - Authorization: Bearer {groqApiKey} (API 인증)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqApiConfig.getGroqApiKey());

        // 3. 사용자 메시지 객체를 생성합니다.
        //    - role: "user"
        //    - content: 시스템 프롬프트 템플릿에 newsContent를 삽입하여 생성한 메시지
        Map<String, Object> messageObj = new HashMap<>();
        messageObj.put("role", "user");
        messageObj.put("content", message);

        // 4. API 요청 본문을 구성합니다.
        //    - model: 사용할 모델명 (groqConfig.getGroqModel())
        //    - messages: 사용자 메시지 객체를 리스트 형태로 포함
        //    - temperature: 응답 생성 시 사용할 온도 (groqConfig.getGroqTemperature())
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", groqApiConfig.getGroqModel());
        requestBody.put("messages", List.of(messageObj));
        requestBody.put("temperature", groqApiConfig.getGroqTemperature());

        try {
            // 5. RestClient를 사용하여 Groq API에 POST 요청을 보냅니다.
            //    - URI: groqConfig.getGroqApiUrl()
            //    - 헤더: 구성된 headers 추가
            //    - 요청 본문: requestBody (JSON 형식)
            //    - 응답을 JSON String 형태로 반환받습니다.
            String responseJson = restClient.post()
                    .uri(groqApiConfig.getGroqApiUrl())
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

//            System.out.println(responseJson);

            // 6. Jackson ObjectMapper를 사용하여 응답 JSON 문자열을 JsonNode로 파싱합니다.
            JsonNode rootNode = objectMapper.readTree(responseJson);

            // 7. JsonNode에서 choices 배열의 첫 번째 요소의 message.content 값을 추출합니다.
            String content = rootNode.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            // 8. 추출한 content를 "description" 키로 담은 Map을 생성하여 반환합니다.
            Map<String, Object> result = new HashMap<>();
            result.put("description", content);
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 9. 예외 발생 시 "No content" 메시지를 포함한 Map 반환.
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("description", "No content");
        return fallback;
    }
}
