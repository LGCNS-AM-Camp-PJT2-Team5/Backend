package com.team9.jobbotdari.controller;

import com.team9.jobbotdari.dto.request.InterestRequestDto;
import com.team9.jobbotdari.dto.response.UserInterestResponseDto;
import com.team9.jobbotdari.service.UserInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/interests")
@RequiredArgsConstructor
public class UserInterestController {

    private final UserInterestService userInterestService;

    @GetMapping
    public ResponseEntity<UserInterestResponseDto> getUserInterests(
            @RequestHeader("X-User-Id") String userId) {

        UserInterestResponseDto userInterestResponseDto =
                userInterestService.getUserInterests(Long.parseLong(userId));
        return ResponseEntity.ok(userInterestResponseDto);
    }

    @PostMapping
    public ResponseEntity<String> addUserInterest(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody InterestRequestDto interestRequestDto) {

        userInterestService.addUserInterest(Long.parseLong(userId), interestRequestDto);
        return ResponseEntity.ok("관심 기업 추가 완료");
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<String> deleteUserInterest(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("companyId") Long companyId) {

        userInterestService.deleteUserInterest(Long.parseLong(userId), companyId);
        return ResponseEntity.ok("관심 기업 삭제 완료");
    }
}
