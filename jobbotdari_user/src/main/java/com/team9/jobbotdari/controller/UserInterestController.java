package com.team9.jobbotdari.controller;

import com.team9.jobbotdari.dto.request.AddInterestRequest;
import com.team9.jobbotdari.service.UserInterestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/interests")
@RequiredArgsConstructor
public class UserInterestController {

    private final UserInterestService userInterestService;

    @PostMapping
    public ResponseEntity<String> addInterest(@RequestBody AddInterestRequest addInterestRequest) {
        userInterestService.addUserInterest(addInterestRequest);
        return ResponseEntity.ok("관심 기업 추가 완료");
    }
}
