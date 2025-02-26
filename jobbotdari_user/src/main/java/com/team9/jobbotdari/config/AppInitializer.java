package com.team9.jobbotdari.config;

import com.team9.jobbotdari.dto.request.SignupRequestDto;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.entity.enums.Role;
import com.team9.jobbotdari.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppInitializer {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        String adminUsername = "admin";

        // 이미 존재하는 관리자 계정이 있는지 확인
        if (userRepository.existsByUsername(adminUsername)) {
            System.out.println("관리자 계정이 이미 존재합니다.");
            return;
        }

        // 관리자가 없으면 새로 생성
        System.out.println("관리자 계정을 생성합니다.");

        SignupRequestDto adminDto = new SignupRequestDto(adminUsername, "admin", "Admin123!", "Admin123!");

        // 비밀번호 암호화
        String encryptedPassword = passwordEncoder.encode(adminDto.getPassword());

        User user = User.builder()
                .name(adminDto.getName())
                .username(adminDto.getUsername())
                .password(encryptedPassword)
                .role(Role.ADMIN) // 🚨 관리자 계정이므로 Role.ADMIN 설정
                .build();

        userRepository.save(user);
        System.out.println("✅ 관리자 계정 생성 완료!");
    }
}
