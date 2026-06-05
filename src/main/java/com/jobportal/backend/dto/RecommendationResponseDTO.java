package com.jobportal.backend.dto;

import java.util.List;


public class RecommendationResponseDTO {

    private List<RecommendedJobDTO> recommendedJobs;

    public RecommendationResponseDTO() {
    }

    public List<RecommendedJobDTO> getRecommendedJobs() {
        return recommendedJobs;
    }

    public void setRecommendedJobs(
            List<RecommendedJobDTO> recommendedJobs
    ) {
        this.recommendedJobs = recommendedJobs;
    }
}