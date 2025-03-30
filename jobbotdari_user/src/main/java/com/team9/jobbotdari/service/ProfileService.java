package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.ProfileUpdateRequestDto;
import com.team9.jobbotdari.dto.response.ProfileResponseDto;
import com.team9.jobbotdari.entity.File;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.exception.user.PasswordMismatchException;
import com.team9.jobbotdari.exception.user.UserNotFoundException;
import com.team9.jobbotdari.repository.FileRepository;
import com.team9.jobbotdari.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public ProfileResponseDto getProfile(Long userId) {
        // 현재 로그인된 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // 최근 프로필 사진을 S3에서 URL로 가져오기
        File file = fileRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId()).orElse(null);
        String fileUrl = (file != null) ? file.getFilePath() : null;

        // 사용자 정보와 함께 파일 URL 반환
        return new ProfileResponseDto(
                user.getId(),
                user.getName(),
                user.getUsername(),
                fileUrl
        );
    }

    @Transactional
    public void updateProfile(Long userId, ProfileUpdateRequestDto requestDto, MultipartFile file) {
        // 현재 로그인된 사용자 조회
        User user = userRepository.findById(userId)
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

        fileService.updateFile(file, user);
    }
}
