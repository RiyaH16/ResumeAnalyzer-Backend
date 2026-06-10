package com.jobportal.backend.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.jobportal.backend.dto.ProfileUpdateRequest;
import com.jobportal.backend.entity.ResumeAnalysis;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.repository.ResumeAnalysisRepository;
import com.jobportal.backend.repository.UserRepository;
import com.jobportal.backend.dto.ChangePasswordRequest;
import com.jobportal.backend.service.AiService;
import com.jobportal.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
	private final ResumeAnalysisRepository analysisRepository;

    @GetMapping("/me")
    public Object getProfile(
            Principal principal
    ) {
        return userService.getProfile(
                principal.getName()
        );
    }

    @PutMapping("/update-profile")
    public Object updateProfile(
            @RequestBody ProfileUpdateRequest request,
            Principal principal
    ) {
        return userService.updateProfile(
                principal.getName(),
                request
        );
    }

    @PutMapping("/change-password")
    public String changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Principal principal
    ) {
        return userService.changePassword(
                principal.getName(),
                request
        );
    }
    
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository
                .findByEmail(email)
                .orElseThrow();

        Map<String, Object> stats = new HashMap<>();

        stats.put(
            "resumeUploaded",
            user.getResumeUrl() != null ? 1 : 0
        );

        int atsScore = 0;

        List<ResumeAnalysis> analyses =
               analysisRepository.findByEmail(email);

        if (!analyses.isEmpty()) {

            atsScore =
                analyses.get(analyses.size() - 1)
                        .getAtsScore();
        }

        stats.put("atsScore", atsScore);

        stats.put(
            "coverLetters",
            user.getCoverLetterCount()
        );

        return ResponseEntity.ok(stats);
    }
}