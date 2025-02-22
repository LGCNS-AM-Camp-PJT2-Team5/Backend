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

    @Value("${file.upload-dir}")
    private String uploadDir;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ProfileResponseDto profile = profileService.getProfile(userDetails);
        return ResponseEntity.ok().body(Map.of("code", 200, "data", profile));
    }

    @PatchMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestPart ProfileUpdateRequestDto requestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        profileService.updateProfile(userDetails, requestDto, file);
        return ResponseEntity.ok().body(Map.of("code", 200, "data", true));
    }

    @GetMapping("/files/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            // 파일 경로 생성
            Path filePath = Paths.get(uploadDir).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                // 파일명 URL 인코딩 (RFC 5987 방식)
                String encodedFilename = URLEncoder.encode(resource.getFilename(), StandardCharsets.UTF_8)
                        .replace("+", "%20");

                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG) // 기본 MIME 타입 (PNG, JPG 지원 가능)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encodedFilename)
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
