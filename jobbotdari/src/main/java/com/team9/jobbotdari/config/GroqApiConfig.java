package com.team9.jobbotdari.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GroqApiConfig {
    /**
     * groqApiUrl:
     * Groq API 엔드포인트 URL
     * 외부 설정 파일의 "groq.api-url" 속성에서 값을 주입받습니다.
     */
    @Value("${groq.api-url}")
    private String groqApiUrl;

    /**
     * groqApiKey:
     * Groq API 호출에 사용할 API 키
     * 외부 설정 파일의 "groq.api-key" 속성에서 값을 주입받습니다.
     */
    @Value("${groq.api-key}")
    private String groqApiKey;


    /**
     * groqModel:
     * Groq API 요청 시 사용할 모델 이름
     * 예) "llama-3.2-3b-preview"
     * 외부 설정 파일의 "groq.model" 속성에서 값을 주입받습니다.
     */
    @Value("${groq.model}")
    private String groqModel;

    /**
     * groqTemperature:
     * Groq API 요청 시 응답 생성에 사용되는 온도 값
     * 온도는 0과 1 사이의 값으로, 낮은 값은 더 결정적인(Deterministic) 응답을, 높은 값은 더 무작위적인 응답을 생성합니다.
     * 외부 설정 파일의 "groq.temperature" 속성에서 값을 주입받습니다.
     */
    @Value("${groq.temperature}")
    private double groqTemperature;

    /**
     * getSystemPromptTemplate:
     * Groq API 요청 시 사용할 시스템 프롬프트 템플릿을 반환하는 메서드
     * 템플릿에는 뉴스 기사를 분석하는 역할과, 요약 방식, 그리고 사용자에게 입력받은 기사 내용을 삽입할 위치가 포함되어 있습니다.
     *
     * 반환되는 문자열에는 %s 플레이스홀더가 포함되어 있으며, 실제 사용 시 해당 부분에 뉴스 기사 내용을 포맷팅하여 삽입하게 됩니다.
     *
     * @return 시스템 프롬프트 템플릿 문자열
     */
    public String getSystemPromptTemplate() {
        return """
                <|start_header_id|>system<|end_header_id|>
                
                당신은 취업준비생을 위한 기업 분석 전문가입니다. 제공된 뉴스 기사를 분석하여, 해당 기업이 최근 어떤 이슈에 주목하고 있으며, 경영 전략, 신사업, 기술 투자, 시장 동향 등을 파악해 주세요.
                답변은 반드시 한국어로 작성하며, 아래 요약 방식을 준수해 주세요.
                
                요약 방식:
                1. **핵심 내용 요약**: 기사에서 다루는 주요 내용을 3~5줄 이내로 요약합니다.
                2. **기업이 주목하는 키워드 및 방향성**: 해당 기업이 어떤 기술, 시장, 정책, 전략 등에 집중하고 있는지 정리합니다.
                3. **취업준비생 관점 분석**: 이 뉴스를 통해 취업준비생이 얻을 수 있는 인사이트를 제공합니다. (예: 기업이 원하는 인재상, 기술 스택, 관련 직무 전망 등)
                4. **활용 팁**: 지원서나 면접에서 활용할 수 있는 포인트를 제안합니다.
                
                <|start_header_id|>user<|end_header_id|>
                
                입력 기사:
                %s
                
                <|start_header_id|>assistant<|end_header_id|>
                """;
    }
}
