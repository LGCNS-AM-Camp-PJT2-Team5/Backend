package com.team9.jobbotdari.repository;

import com.team9.jobbotdari.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, Long> {
}
