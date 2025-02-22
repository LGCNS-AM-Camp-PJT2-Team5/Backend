package com.team9.jobbotdari.service;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;

import com.team9.jobbotdari.dto.NewsDto;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class NewsService {

    /**
     * 사용자가 입력한 쿼리에 해당하는 뉴스를 검색하여 NewsDto 리스트로 반환하는 메서드
     * Google News의 RSS 피드를 이용하여 최신 뉴스를 가져옵니다.
     *
     * @param query 검색어 (사용자가 입력한 키워드)
     * @return 검색 결과에 해당하는 NewsDto 객체의 리스트
     */
    public List<NewsDto> searchNews(String query) {
        // 결과를 저장할 리스트 생성
        List<NewsDto> newsDtoList = new ArrayList<>();
        try {
            // 검색어(query)를 URL 인코딩하여 특수문자 처리
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);

            // 구글 뉴스 RSS URL 구성
            // hl=ko: 한국어, gl=KR: 한국, ceid=KR:ko: 한국어 버전의 뉴스
            String rssUrl = "https://news.google.com/rss/search?q=" + encodedQuery + "&hl=ko&gl=KR&ceid=KR:ko";

            // RSS URL을 URI로 변환한 후 URL 객체로 생성
            URL feedUrl = new URI(rssUrl).toURL();

            // RSS 피드를 읽어오기 위한 SyndFeedInput 객체 생성
            SyndFeedInput input = new SyndFeedInput();

            // XmlReader를 사용하여 RSS 피드를 읽어옴. try-with-resources 구문을 사용하여 스트림 자동 종료
            try (XmlReader xmlReader = new XmlReader(feedUrl.openStream())) {
                // RSS 피드를 파싱하여 SyndFeed 객체로 변환
                SyndFeed feed = input.build(xmlReader);

                // SyndFeed에서 각 뉴스 항목(entry)을 순회
                for (SyndEntry entry : feed.getEntries()) {

                    // 가져오는 뉴스 항목 갯수 설정
//                    if (newsDtoList.size() >= 5) break;

                    // NewsDto 객체 생성 (dto 클래스)
                    NewsDto newsDto = new NewsDto();

                    // 뉴스 제목 설정
                    newsDto.setTitle(entry.getTitle());
                    // 뉴스 링크(원문 URL) 설정
                    newsDto.setLink(entry.getLink());

                    // 뉴스 발행일 설정
                    newsDto.setPublishedDate(entry.getPublishedDate());

                    // 리스트에 생성한 NewsDto 객체 추가
                    newsDtoList.add(newsDto);
                }
            }
        } catch (Exception e) {
            // 예외 발생 시 스택 트레이스 출력 (실제 운영 시 로깅 등으로 대체 가능)
            e.printStackTrace();
        }
        // 최종적으로 뉴스 기사 리스트 반환
        return newsDtoList;
    }

    /**
     * 뉴스 기사 리스트의 각 타이틀 앞에 번호를 붙여 하나의 문자열로 결합합니다.
     * 예)
     * 1. 첫번째 뉴스 제목
     * 2. 두번째 뉴스 제목
     * 3. 세번째 뉴스 제목
     *
     * @param newsList 뉴스 기사 리스트
     * @return 번호가 붙은 뉴스 타이틀들의 결합 문자열
     */
    public String generateTitlesSummaryInput(List<NewsDto> newsList) {
        StringBuilder titlesBuilder = new StringBuilder();
//        for (int i = 0; i < news.size(); i++) {
        // API 토큰 갯수 제한으로 인해 뉴스 제목 5개만 반환하도록 설정
        for (int i = 0; i < 5; i++) {
            // 번호와 뉴스 제목을 추가
            titlesBuilder.append(i + 1).append(". ").append(newsList.get(i).getTitle());
            // 마지막 항목이 아니라면 줄바꿈 추가
            if (i < newsList.size() - 1) {
                titlesBuilder.append("\n");
            }
        }
        return titlesBuilder.toString();
    }
}
