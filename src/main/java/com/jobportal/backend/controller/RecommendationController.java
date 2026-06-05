package com.jobportal.backend.controller;

import com.jobportal.backend.service.RecommendationService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
@CrossOrigin("*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(
            RecommendationService recommendationService
    ) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> recommendJobs(

            @PathVariable Long userId

    ) {

        return ResponseEntity.ok(
                recommendationService.recommendJobs(userId)
        );
    }
}