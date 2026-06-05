package com.jobportal.backend.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.backend.dto.ForgotPasswordRequest;
import com.jobportal.backend.dto.LoginRequest;
import com.jobportal.backend.dto.LoginResponse;
import com.jobportal.backend.dto.RegisterRequest;
import com.jobportal.backend.entity.Role;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.repository.UserRepository;
import com.jobportal.backend.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

	public String register(RegisterRequest request) {

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        if (
        	    userRepository.findByEmail(
        	        request.getEmail()
        	    ).isPresent()
        	) {

        	    throw new RuntimeException(
        	        "Email already registered"
        	    );
        	}
        
        userRepository.save(user);

        return "User registered successfully";
    }

	public LoginResponse login(LoginRequest request) {

	    User user = userRepository.findByEmail(request.getEmail())
	            .orElseThrow(() -> new RuntimeException("User not found"));

	    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
	        throw new RuntimeException("Invalid password");
	    }

	    String token = jwtUtil.generateToken(user.getEmail());

	    return new LoginResponse(
	            token,
	            user.getName(),
	            user.getEmail()
	    );
	}
	
	public String forgotPassword(
	        ForgotPasswordRequest request
	) {

	    User user = userRepository
	            .findByEmail(request.getEmail())
	            .orElseThrow(() ->
	                    new RuntimeException("User not found"));

	    user.setPassword(
	            passwordEncoder.encode(
	                    request.getNewPassword()
	            )
	    );

	    userRepository.save(user);

	    return "Password Reset Successfully";
	}
}