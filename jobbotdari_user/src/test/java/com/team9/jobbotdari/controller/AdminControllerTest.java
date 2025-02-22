package com.team9.jobbotdari.controller;

import com.team9.jobbotdari.common.jwt.JwtUtils;
import com.team9.jobbotdari.dto.response.UserListResponseDto;
import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.entity.enums.Role;
import com.team9.jobbotdari.exception.user.UserNotFoundException;
import com.team9.jobbotdari.repository.LogRepository;
import com.team9.jobbotdari.repository.UserRepository;
import com.team9.jobbotdari.security.CustomUserDetails;
import com.team9.jobbotdari.service.AdminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.data.jpa.repositories.enabled=false"  // JPA 리포지토리 스캔 비활성화 (TestConfig에서 등록한 Mockito mock 빈이 사용되도록)
})
@AutoConfigureMockMvc
@Import(AdminControllerTest.TestConfig.class)
public class AdminControllerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        @Primary
        public AdminService adminService() {
            return Mockito.mock(AdminService.class);
        }

        @Bean
        @Primary
        public UserRepository userRepository() {
            return Mockito.mock(UserRepository.class);
        }

        @Bean
        @Primary
        public LogRepository logRepository() {
            return Mockito.mock(LogRepository.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AdminService adminService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtils jwtUtils;

    @Test
    @DisplayName("JWT 토큰으로 관리자 권한 확인")
    public void testGetUsersWithJwtToken() throws Exception {
        // 관리자 권한 Mock User 생성
        User mockAdminUser = User.builder()
                .id(1L)
                .username("admin")
                .name("관리자")
                .password("password")
                .role(Role.ADMIN)  // 관리자 권한 설정
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // UserRepository - findByUsername 메서드 모킹
        when(userRepository.findByUsername("admin")).thenReturn(mockAdminUser);

        // JWT 토큰 생성 (관리자 권한 포함)
        CustomUserDetails userDetails = new CustomUserDetails(mockAdminUser);
        String jwtToken = jwtUtils.generateToken(userDetails);

        // AdminService - getUserList 메서드 모킹
        List<UserListResponseDto> userList = Collections.singletonList(
                UserListResponseDto.builder()
                        .id(1L)
                        .username("admin")
                        .role(Role.ADMIN)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );
        when(adminService.getUserList()).thenReturn(userList);

        // JWT 토큰을 사용하여 /admin/users API 호출 (관리자 권한 확인)
        mockMvc.perform(MockMvcRequestBuilders.get("/admin/users")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }


    @Test
    @DisplayName("JWT 토큰으로 관리자 권한 확인 - 존재하지 않는 유저 삭제 시 404 응답")
    public void testDeleteUserNotFound() throws Exception {
        // 관리자 권한 Mock User 생성
        User mockAdminUser = User.builder()
                .id(1L)
                .username("admin")
                .name("관리자")
                .password("password")
                .role(Role.ADMIN)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        // UserRepository - findByUsername 모킹
        when(userRepository.findByUsername("admin")).thenReturn(mockAdminUser);

        // JWT 토큰 생성 (관리자 권한 포함)
        CustomUserDetails userDetails = new CustomUserDetails(mockAdminUser);
        String jwtToken = jwtUtils.generateToken(userDetails);

        // 존재하지 않는 userId에 대해 deleteUserById 호출 시 UserNotFoundException 발생하도록 모킹
        Long nonExistingUserId = 999L;
        doThrow(new UserNotFoundException())
                .when(adminService).deleteUserById(nonExistingUserId);

        // DELETE /admin/users/{userId} API 호출 및 404 응답 검증
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/users/{userId}", nonExistingUserId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andDo(print());
    }
}
