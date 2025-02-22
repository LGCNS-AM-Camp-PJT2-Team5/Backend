package com.team9.jobbotdari.repository;

import com.team9.jobbotdari.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, Long> {
    // userId 기준으로 가장 최신 파일 1개 조회
    Optional<File> findTopByUserIdOrderByCreatedAtDesc(Long userId);
}
