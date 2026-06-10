package com.jobportal.backend.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;
    
    @Column(name = "resume_url")
    private String resumeUrl;
    
    @Column(name = "resume_publicID")
    private String resumePublicId;
    
    @Builder.Default
    @Column(name = "cover_letter_count")
    private int coverLetterCount = 0;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "TEXT")
    private String resumeAnalysis;
    
    
    
    
}