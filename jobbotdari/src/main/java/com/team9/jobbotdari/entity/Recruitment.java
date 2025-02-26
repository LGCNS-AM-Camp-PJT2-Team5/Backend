package com.team9.jobbotdari.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "recruitment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recruitment {
    @Id
    private Long id;

    private String title;

    private String requirements;    // 자격 요건 (ex. 신입, 경력)

    @Column(columnDefinition = "TEXT")
    private String description;     // 상세 정보

    private LocalDateTime deadline;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
