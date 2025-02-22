package com.team9.jobbotdari.service;


import com.team9.jobbotdari.entity.File;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    @Value("${file.upload-dir}")
    private String uploadDir;
    private final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif"); // 허용할 확장자 목록

    public void saveFile(MultipartFile multipartFile, User user) {

        updateFile(multipartFile, user);
    }

    public void updateFile(MultipartFile multipartFile, User user) {
        if (multipartFile.isEmpty()) {
            return;
        }

        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("허용되지 않은 파일 형식입니다. (지원 형식: jpg, jpeg, png, gif)");
            }

            Path filePath = Paths.get(uploadDir, originalFilename);
            java.io.File file = filePath.toFile();
            file.getParentFile().mkdirs(); // 디렉토리 생성

            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(multipartFile.getBytes());
            }

            // 기존 파일 삭제 후 새로운 파일 저장
            fileRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId()).ifPresent(fileRepository::delete);

            File savedFile = File.builder()
                    .user(user)
                    .filename(originalFilename)
                    .filePath(filePath.toString())
                    .build();

            fileRepository.save(savedFile);

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }
    }

    // 파일 확장자 추출 메서드
    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1 || dotIndex == filename.length() - 1) {
            return ""; // 확장자가 없거나 잘못된 경우
        }
        return filename.substring(dotIndex + 1);
    }
}