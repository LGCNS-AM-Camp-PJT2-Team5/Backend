package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.ProfileUpdateRequestDto;
import com.team9.jobbotdari.dto.response.ProfileResponseDto;
import com.team9.jobbotdari.entity.File;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.exception.user.PasswordMismatchException;
import com.team9.jobbotdari.exception.user.UserNotFoundException;
import com.team9.jobbotdari.repository.FileRepository;
import com.team9.jobbotdari.repository.UserRepository;
import com.team9.jobbotdari.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProfileService {
    private final UserRepository userRepository;
    private final FileRepository fileRepository;
    private final FileService fileService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${file.access-url}")
    private String fileAccessUrl;  // S3의 기본 URL 경로

    public ProfileResponseDto getProfile(CustomUserDetails userDetails) {
        // 현재 로그인된 사용자 조회
        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(UserNotFoundException::new);

        // 최근 프로필 사진을 S3에서 URL로 가져오기
        File file = fileRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId()).orElse(null);

        // S3에서 가져온 파일 URL 생성
        String fileUrl = (file != null) ? fileAccessUrl + file.getFilename() : null;

        // 사용자 정보와 함께 파일 URL 반환
        return new ProfileResponseDto(
                user.getId(),
                user.getName(),
                user.getUsername(),
                fileUrl
        );
    }

    @Transactional
    public String updateProfile(CustomUserDetails userDetails, ProfileUpdateRequestDto requestDto, MultipartFile file) {
        // 현재 로그인된 사용자 조회
        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(UserNotFoundException::new);

        // 사용자 정보 업데이트 (이름, 비밀번호)
        if (requestDto != null) {
            if (requestDto.getName() != null && !requestDto.getName().isEmpty()) {
                user.setName(requestDto.getName());
            }

            if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
                if (requestDto.getPasswordConfirm() != null && !requestDto.getPasswordConfirm().isEmpty()) {
                    if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
                        throw new PasswordMismatchException();
                    }
                    user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
                }
            }
        }

        // 파일 변경 처리 (S3 업로드)
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            // 기존 파일을 삭제 (필요한 경우)
            File oldFile = fileRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId()).orElse(null);
            if (oldFile != null) {
                fileService.deleteFileFromS3(oldFile); // 기존 파일을 S3에서 삭제
            }

            // 새로운 파일을 S3에 업로드하고 URL 반환
            try {
                fileUrl = fileService.uploadFileToS3(file, user);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // 사용자 정보 저장
        userRepository.save(user);

        // 업로드된 파일 URL 반환
        return fileUrl;
    }
}
