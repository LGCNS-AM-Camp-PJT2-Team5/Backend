package com.team9.jobbotdari.repository;

import com.team9.jobbotdari.entity.Company;
import com.team9.jobbotdari.entity.Recruitment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitmentRepository extends JpaRepository<Recruitment, Long> {
}
