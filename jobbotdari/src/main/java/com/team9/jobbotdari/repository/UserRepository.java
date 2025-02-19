package com.team9.jobbotdari.repository;

import com.team9.jobbotdari.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
