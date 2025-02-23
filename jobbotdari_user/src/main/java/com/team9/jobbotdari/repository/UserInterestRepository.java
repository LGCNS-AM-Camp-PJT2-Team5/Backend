package com.team9.jobbotdari.repository;

import com.team9.jobbotdari.entity.User;
import com.team9.jobbotdari.entity.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserInterestRepository extends JpaRepository<UserInterest, Long> {
    boolean existsByUserAndCompanyId(User user, Long companyId);
    List<UserInterest> findByUser(User user);
    void deleteByUserAndCompanyId(User user, Long companyId);
}
