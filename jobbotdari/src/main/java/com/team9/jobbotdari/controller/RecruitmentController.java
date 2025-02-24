package com.team9.jobbotdari.controller;

import com.team9.jobbotdari.dto.response.RecruitmentResponseDto;
import com.team9.jobbotdari.service.RecruitmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recruitment")
@RequiredArgsConstructor
public class RecruitmentController {
    private final RecruitmentService recruitmentService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getRecruitments() {
        List<RecruitmentResponseDto> recruitments = recruitmentService.getAllRecruitments();
        return ResponseEntity.ok(Map.of("code", 200, "data", Map.of("companies", recruitments)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRecruitmentById(@PathVariable("id") Long id) {
        RecruitmentResponseDto recruitment = recruitmentService.getRecruitmentById(id);
        return ResponseEntity.ok(Map.of("code", 200, "data", recruitment));
    }
}
