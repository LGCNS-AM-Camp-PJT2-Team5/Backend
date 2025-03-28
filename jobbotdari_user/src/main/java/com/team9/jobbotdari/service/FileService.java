package com.team9.jobbotdari.service;


import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.team9.jobbotdari.entity.File;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;
    private final AmazonS3Client amazonS3;

    @Value("${aws.s3.bucket.name}")
    private String bucketName;

    // 허용할 확장자 목록
    private final List<String> ALLOWED_EXTENSIONS = List.of("jpg", "jpeg", "png", "gif");

    // 파일 저장 메서드
    public void saveFile(MultipartFile multipartFile, User user) {
        updateFile(multipartFile, user);
    }

    // 파일 업데이트 메서드
    public void updateFile(MultipartFile multipartFile, User user) {
        if (multipartFile.isEmpty()) {
            return; // 파일이 비어 있으면 아무 작업도 하지 않음
        }

        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String extension = getFileExtension(originalFilename);

            // 확장자가 허용된 형식인지 체크
            if (!ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                throw new IllegalArgumentException("허용되지 않은 파일 형식입니다. (지원 형식: jpg, jpeg, png, gif)");
            }

            // S3에 파일 업로드
            String fileName = generateUniqueFileName(originalFilename);
            uploadFileToS3(multipartFile, fileName);

            // 기존 파일 삭제 (사용자가 프로필을 변경할 때)
            fileRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                    .ifPresent(existingFile -> fileRepository.delete(existingFile));

            // S3 URL 생성
            String filePath = amazonS3.getUrl(bucketName, fileName).toString();

            // 파일 정보 저장 (DB)
            File savedFile = File.builder()
                    .user(user)
                    .filename(originalFilename)
                    .filePath(filePath)
                    .build();

            fileRepository.save(savedFile);

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 중 오류 발생", e);
        }
    }

    // 파일 이름을 UUID로 생성하여 중복을 방지
    private String generateUniqueFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString() + "." + extension;
    }

    // S3에 파일 업로드
    private void uploadFileToS3(MultipartFile multipartFile, String fileName) throws IOException {
        try (InputStream inputStream = multipartFile.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());

            // S3에 파일 업로드
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));
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
    public void deleteFileFromS3(File file) {
        try {
            String fileName = file.getFilename();
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, fileName));
            log.info("파일이 S3에서 삭제되었습니다: {}", fileName);
        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("파일 삭제 중 오류 발생", e);
        }
    }

    public String uploadFileToS3(MultipartFile multipartFile, User user) throws IOException {
        // 파일 이름을 고유하게 생성하여 중복 방지
        String fileName = generateUniqueFileName(multipartFile.getOriginalFilename());

        try (InputStream inputStream = multipartFile.getInputStream()) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(multipartFile.getContentType());
            metadata.setContentLength(multipartFile.getSize());

            // S3에 파일 업로드
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));

            // 업로드한 파일의 URL 반환
            return amazonS3.getUrl(bucketName, fileName).toString();
        } catch (IOException e) {
            log.error("파일 업로드 중 오류 발생: {}", e.getMessage());
            throw new IOException("파일 업로드 중 오류 발생", e);
        }
    }

}
