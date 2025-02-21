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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private String title;

    private String requirements;

    @Column(columnDefinition = "TEXT")
    private String description;

    private LocalDateTime deadline;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
