package com.jobportal.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "resume_analysis")
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column
    private String email;
    
    @Column(name = "ats_score")
    private int atsScore;

    @Column(name = "analysis_json", columnDefinition = "TEXT")
    private String analysisJson;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}