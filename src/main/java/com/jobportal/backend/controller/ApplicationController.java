package com.jobportal.backend.controller;

import java.util.List;
import com.jobportal.backend.entity.Application;
import com.jobportal.backend.service.ApplicationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService
    ) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply/{jobId}")
    public ResponseEntity<?> applyJob(
            @PathVariable Long jobId
    ) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(
                applicationService.applyByEmail(
                        email,
                        jobId
                )
        );
    }
    
    @GetMapping("/my-applications")
    public ResponseEntity<?> myApplications() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        return ResponseEntity.ok(
                applicationService
                        .getApplicationsByEmail(email)
        );
    }
}