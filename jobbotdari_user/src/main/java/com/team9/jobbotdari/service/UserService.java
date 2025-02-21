package com.team9.jobbotdari.service;

import com.team9.jobbotdari.dto.request.SignupRequestDto;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.entity.enums.Role;
import com.team9.jobbotdari.exception.signup.SignupException;
import com.team9.jobbotdari.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final FileService fileService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public void registerUser(SignupRequestDto request, MultipartFile file) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new SignupException.DuplicateUsernameException();
        }

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new SignupException.PasswordMismatchException();
        }

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .password(encryptedPassword)
                .role(Role.USER) // 기본적으로 USER 역할 부여
                .build();

        userRepository.save(user);

        // 파일이 있는 경우에만 저장
        if (file != null && !file.isEmpty()) {
            fileService.saveFile(file, user);
        }
    }
}
