package com.team9.jobbotdari.controller;

import com.team9.jobbotdari.dto.request.InterestRequestDto;
import com.team9.jobbotdari.dto.response.UserInterestResponseDto;
import com.team9.jobbotdari.entity.User;
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
    public ResponseEntity<UserInterestResponseDto> getUserInterests() {
        UserInterestResponseDto userInterestResponseDto = userInterestService.getUserInterests();
        return ResponseEntity.ok(userInterestResponseDto);
    }

    @PostMapping
    public ResponseEntity<String> addUserInterest(@RequestBody InterestRequestDto interestRequestDto) {
        userInterestService.addUserInterest(interestRequestDto);
        return ResponseEntity.ok("관심 기업 추가 완료");
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<String> deleteUserInterest(@PathVariable("companyId") Long companyId) {
        userInterestService.deleteUserInterest(companyId);
        return ResponseEntity.ok("관심 기업 삭제 완료");
    }
}
