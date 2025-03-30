package com.team9.jobbotdari.controller;

import com.team9.jobbotdari.dto.request.ProfileUpdateRequestDto;
import com.team9.jobbotdari.dto.response.ProfileResponseDto;
import com.team9.jobbotdari.security.CustomUserDetails;
import com.team9.jobbotdari.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProfileController {
    private final ProfileService profileService;

    @Value("${file.access-url}")
    private String fileAccessUrl; // S3 접근 URL

    // 사용자 프로필 조회
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ProfileResponseDto profile = profileService.getProfile(userDetails);
        return ResponseEntity.ok().body(Map.of("code", 200, "data", profile));
    }

    // 프로필 수정 (이름, 비밀번호 변경 및 파일 업로드)
    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestPart(required = false) ProfileUpdateRequestDto requestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        // 프로필 업데이트 후 파일 URL 반환
        profileService.updateProfile(userDetails, requestDto, file);
        return ResponseEntity.ok().body(Map.of("code", 200));
    }

    // S3에서 파일 제공 (파일 다운로드)
    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // S3 URL로부터 파일을 다운로드
            String s3FileUrl = fileAccessUrl + filename;  // S3 URL
            UrlResource resource = new UrlResource(s3FileUrl);  // S3 파일을 리소스로 변환

            // 파일이 존재하면 다운로드 응답을 반환
            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\"")
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();  // 파일이 없으면 404
            }

        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();  // URL이 잘못된 경우 400
        }
    }
}
