package com.team9.jobbotdari.repository;

import com.team9.jobbotdari.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LogRepository extends JpaRepository<Log, Long> {
    List<Log> findByCreatedAtAfter(LocalDateTime date);
}

