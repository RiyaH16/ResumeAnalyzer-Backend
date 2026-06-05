package com.jobportal.backend.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.*;

import com.jobportal.backend.dto.ProfileUpdateRequest;
import com.jobportal.backend.dto.ChangePasswordRequest;
import com.jobportal.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

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
}