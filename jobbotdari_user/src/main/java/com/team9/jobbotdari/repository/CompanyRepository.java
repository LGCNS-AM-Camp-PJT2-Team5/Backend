package com.team9.jobbotdari.repository;

import com.team9.jobbotdari.entity.Company;
import com.team9.jobbotdari.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
