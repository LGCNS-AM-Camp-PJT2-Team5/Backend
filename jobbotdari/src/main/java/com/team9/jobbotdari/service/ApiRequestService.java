package com.team9.jobbotdari.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team9.jobbotdari.config.GroqApiConfig;
import com.team9.jobbotdari.config.SaraminApiConfig;
import com.team9.jobbotdari.dto.request.RecruitmentRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ApiRequestService {
    private final GroqApiConfig groqApiConfig;
    private final SaraminApiConfig saraminApiConfig;

    // RestClient 인스턴스를 생성하여 HTTP 요청을 수행합니다.
    private final RestClient restClient = RestClient.create();

    // JSON 파싱을 위한 Jackson ObjectMapper 인스턴스
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 뉴스 콘텐츠를 기반으로 Groq API에 요청을 보내고, 응답에서 요약된 내용을 추출하여 반환합니다.
     *
     * 요청 과정:
     * 1. 시스템 프롬프트 템플릿에 newsContent를 삽입하여 최종 메시지를 생성
     * 2. HTTP 헤더를 설정하여 JSON 요청을 준비 (Bearer 인증 포함)
     * 3. 사용자 메시지 객체 생성 (role: "user", content: newsContent 삽입)
     * 4. API 요청 본문을 구성 (모델명, 메시지 리스트, temperature 설정 포함)
     * 5. Groq API에 POST 요청을 보내고, 응답 JSON 문자열을 반환받음
     * 6. 응답 JSON에서 요약된 내용을 추출하여 "description" 키로 Map에 저장
     * 7. 예외 발생 시 "No content" 메시지를 포함한 기본 Map 반환
     *
     * @param newsContent 뉴스 콘텐츠 (기사 링크 또는 기사 요약)
     * @return Groq API 응답에서 추출한 요약 내용이 포함된 Map 객체
     */
    public Map<String, Object> getNewsSummary(String newsContent) {
        // 1. 시스템 프롬프트 템플릿에 newsContent를 삽입하여 최종 메시지를 생성
        String message = String.format(groqApiConfig.getSystemPromptTemplate(), newsContent);

        // 2. HTTP 요청 헤더 설정 (JSON 형식 및 Bearer 인증 포함)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + groqApiConfig.getGroqApiKey());

        // 3. 사용자 메시지 객체 생성
        Map<String, Object> messageObj = new HashMap<>();
        messageObj.put("role", "user");
        messageObj.put("content", message);

        // 4. API 요청 본문 구성
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", groqApiConfig.getGroqModel());
        requestBody.put("messages", List.of(messageObj));
        requestBody.put("temperature", groqApiConfig.getGroqTemperature());

        try {
            // 5. Groq API에 POST 요청을 보내고 JSON 응답을 받음
            String responseJson = restClient.post()
                    .uri(groqApiConfig.getGroqApiUrl())
                    .headers(httpHeaders -> httpHeaders.addAll(headers))
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return parseNewsSummaryData(responseJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 6. 예외 발생 시 기본 응답 반환
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("description", "No content");
        return fallback;
    }

    /**
     * Groq API 응답에서 요약된 뉴스를 추출하는 메서드
     *
     * @param responseJson Groq API 응답 JSON 문자열
     * @return "description" 키에 요약된 뉴스가 담긴 Map 객체
     */
    public Map<String, Object> parseNewsSummaryData(String responseJson) {
        Map<String, Object> result = new HashMap<>();

        try {
            // JSON 응답을 파싱하여 "choices" 배열의 첫 번째 요소에서 요약 내용 추출
            JsonNode rootNode = objectMapper.readTree(responseJson);
            String content = rootNode.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            result.put("description", content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 사람인 API에서 채용 공고 정보를 가져오는 메서드
     *
     * @return 채용 공고 목록을 RecruitmentRequestDto 리스트로 반환
     */
    public List<RecruitmentRequestDto> getSaraminRecruitmentsInfo() {
        Map<String, Object> queryParams = saraminApiConfig.getSaraminQueryParams();

        try {
            // 1. 사람인 API에 GET 요청을 보내고 JSON 응답을 수신
            String responseJson = restClient.get()
                    .uri(saraminApiConfig.getSaraminApiUrl() + "?access-key={access-key}&job-category={job-category}&count={count}&fields={fields}&sort={sort}",
                            queryParams)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);

            return parseRecruitmentData(responseJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
        // 2. 요청 실패 시 빈 리스트 반환
        return Collections.emptyList();
    }

    /**
     * 사람인 API 응답 데이터를 RecruitmentRequestDto 리스트로 변환하는 메서드
     *
     * @param responseJson 사람인 API 응답 JSON 문자열
     * @return 채용 공고 정보를 담은 RecruitmentRequestDto 리스트
     */
    public List<RecruitmentRequestDto> parseRecruitmentData(String responseJson) {
        List<RecruitmentRequestDto> result = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

        try {
            // 1. JSON 응답을 파싱하여 "jobs" 객체 내부의 "job" 배열 추출
            JsonNode rootNode = objectMapper.readTree(responseJson);
            JsonNode jobNodes = rootNode.path("jobs").path("job");

            if (jobNodes.isArray()) {
                for (JsonNode jobNode : jobNodes) {
                    // 2. 각 채용 공고의 필드 추출
                    Long id = jobNode.path("id").asLong();
                    String companyName = jobNode.path("company").path("detail").path("name").asText();
                    String title = jobNode.path("position").path("title").asText();
                    String requirements = jobNode.path("position").path("job-type").path("name").asText();
                    String description = jobNode.path("url").asText();
                    String deadlineString = jobNode.path("expiration-date").asText();

                    // 3. 마감일 문자열을 LocalDateTime으로 변환
                    LocalDateTime deadline = null;
                    if (!deadlineString.isEmpty()) {
                        deadline = OffsetDateTime.parse(deadlineString, formatter).toLocalDateTime();
                    }

                    // 4. DTO 객체 생성 후 리스트에 추가
                    RecruitmentRequestDto dto = new RecruitmentRequestDto(id, companyName, title, requirements, description, deadline);
                    result.add(dto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
