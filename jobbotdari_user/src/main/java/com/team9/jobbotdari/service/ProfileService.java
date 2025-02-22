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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private String fileAccessUrl;

    public ProfileResponseDto getProfile(CustomUserDetails userDetails) {
        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(UserNotFoundException::new);

        File file = fileRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId()).orElse(null);

        String fileUrl = (file != null) ? fileAccessUrl + file.getFilename() : null;

        return new ProfileResponseDto(
                user.getId(),
                user.getName(),
                user.getUsername(),
                fileUrl
        );
    }

    @Transactional
    public void updateProfile(CustomUserDetails userDetails, ProfileUpdateRequestDto requestDto, MultipartFile file) {
        // 현재 로그인된 사용자 조회
        User user = userRepository.findById(userDetails.getUser().getId())
                .orElseThrow(UserNotFoundException::new);

        // 이름 변경
        if (requestDto.getName() != null && !requestDto.getName().isEmpty()) {
            user.setName(requestDto.getName());
        }

        // 비밀번호 변경 (passwordConfirm 일치 여부 확인)
        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
            if(requestDto.getPasswordConfirm() != null && !requestDto.getPasswordConfirm().isEmpty()) {
                if (!requestDto.getPassword().equals(requestDto.getPasswordConfirm())) {
                    throw new PasswordMismatchException();
                }
                user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
            }
        }

        // 파일 변경
        if (file != null && !file.isEmpty()) {
            fileService.updateFile(file, user);
        }

        userRepository.save(user);
    }
}