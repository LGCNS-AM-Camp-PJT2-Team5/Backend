package com.team9.jobbotdari.controller;


import com.team9.jobbotdari.dto.request.SigninRequestDto;
import com.team9.jobbotdari.dto.request.SignupRequestDto;
import com.team9.jobbotdari.dto.response.SigninResponseDto;
import com.team9.jobbotdari.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> signup(
            @Valid @RequestPart SignupRequestDto signupRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) {

        userService.registerUser(signupRequestDto, file);

        return ResponseEntity.ok().body(Map.of("code", 200, "data", "회원가입이 완료되었습니다"));
    }

    @PostMapping("/signin")
    public ResponseEntity<Map<String, Object>> signin(@Valid @RequestBody SigninRequestDto signinRequestDto) {
        SigninResponseDto signinResponse = userService.authenticateUser(signinRequestDto);

        return ResponseEntity.ok().body(Map.of("code", 200, "data", signinResponse));
    }
}