package com.jobportal.backend.service;

import com.jobportal.backend.dto.RecommendationResponseDTO;
import com.jobportal.backend.entity.Job;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.repository.JobRepository;
import com.jobportal.backend.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final AiService aiService;

    public RecommendationService(
            UserRepository userRepository,
            JobRepository jobRepository,
            AiService aiService
    ) {

        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
        this.aiService = aiService;
    }

    public RecommendationResponseDTO recommendJobs(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        List<Job> jobs = jobRepository.findAll();

        String resumeAnalysis =
                user.getResumeAnalysis();

        StringBuilder jobsData =
                new StringBuilder();

        for (Job job : jobs) {

        	jobsData.append("""
        		    Job ID: %s
        		    Title: %s
        		    Company: %s
        		    Skills: %s
        		    Experience: %s
        		    """
        		    .formatted(
        		        job.getId(),
        		        job.getTitle(),
        		        job.getCompany(),
        		        job.getSkillsRequired(),
        		        job.getMinExperience()
        		));
        }
        

        return aiService.recommendJobs(
                resumeAnalysis,
                jobsData.toString()
        );
    }
}