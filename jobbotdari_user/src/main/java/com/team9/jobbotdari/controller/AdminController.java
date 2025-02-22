package com.team9.jobbotdari.controller;

import com.team9.jobbotdari.dto.response.BaseResponseDto;
import com.team9.jobbotdari.dto.response.LogListResponseDto;
import com.team9.jobbotdari.dto.response.UserListResponseDto;
import com.team9.jobbotdari.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "관리자 API")
@SecurityRequirement(name = "bearerAuth")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    private final int HTTP_STATUS_OK_CODE = HttpStatus.OK.value();

    @GetMapping("/users")
    @Operation(summary = "유저 리스트 조회", description = "모든 유저 정보를 조회합니다. (관리자만 실행 가능)")
    public ResponseEntity<BaseResponseDto> getUsers() {
        List<UserListResponseDto> userList = adminService.getUserList();
        return ResponseEntity.ok(BaseResponseDto.builder()
                .data(userList)
                .code(HTTP_STATUS_OK_CODE)
                .build());
    }

    @DeleteMapping("/users/{userId}")
    @Operation(summary = "유저 삭제", description = "지정한 ID의 유저를 삭제합니다. (관리자만 실행 가능)")
    public ResponseEntity<BaseResponseDto> deleteUser(@PathVariable Long userId) {
        adminService.deleteUserById(userId);
        return ResponseEntity.ok(BaseResponseDto.builder()
                .data("유저 삭제가 완료되었습니다")
                .code(HTTP_STATUS_OK_CODE)
                .build());
    }

    @GetMapping("/logs")
    @Operation(summary = "로그 리스트 조회", description = "시스템 로그 리스트를 조회합니다. (관리자만 실행 가능)")
    public ResponseEntity<BaseResponseDto> getLogs() {
        List<LogListResponseDto> logList = adminService.getLogs();
        return ResponseEntity.ok(BaseResponseDto.builder()
                .data(logList)
                .code(HTTP_STATUS_OK_CODE)
                .build());
    }
}
