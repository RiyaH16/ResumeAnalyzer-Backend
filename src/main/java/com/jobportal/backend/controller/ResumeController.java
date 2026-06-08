package com.jobportal.backend.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.jobportal.backend.dto.ResumeAnalysisDto;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.jobportal.backend.repository.InterviewQuestionRepository;
import com.jobportal.backend.repository.JobRepository;
import com.jobportal.backend.repository.ResumeAnalysisRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.backend.dto.InterviewRequest;
import com.jobportal.backend.dto.RecommendationResponseDTO;
import com.jobportal.backend.entity.InterviewQuestion;
import com.jobportal.backend.entity.Job;
import com.jobportal.backend.entity.ResumeAnalysis;
import com.jobportal.backend.entity.User;
import com.jobportal.backend.repository.UserRepository;
import com.jobportal.backend.security.JwtUtil;
import com.jobportal.backend.service.AiService;
import com.jobportal.backend.util.ResumeParser;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/resume")
public class ResumeController {
	
	private final UserRepository userRepository;
	private final AiService aiService;
	private final ResumeAnalysisRepository analysisRepository;
	private InterviewQuestionRepository interviewRepository;
	private final JwtUtil jwtUtil;
	private final JobRepository jobRepository;
	private final Cloudinary cloudinary;

	public ResumeController(UserRepository userRepository, AiService aiService,
			ResumeAnalysisRepository analysisRepository, InterviewQuestionRepository interviewRepository,
			JwtUtil jwtUtil, JobRepository jobRepository, Cloudinary cloudinary) {
		super();
		this.userRepository = userRepository;
		this.aiService = aiService;
		this.analysisRepository = analysisRepository;
		this.interviewRepository = interviewRepository;
		this.jwtUtil = jwtUtil;
		this.jobRepository = jobRepository;
		this.cloudinary = cloudinary;
	}

	@PostMapping(value = "/upload", consumes = "multipart/form-data")
	public ResponseEntity<?> uploadResume(
	        @RequestParam("file") MultipartFile file) {

	    try {

	        String email = SecurityContextHolder
	                .getContext()
	                .getAuthentication()
	                .getName();

	        User user = userRepository.findByEmail(email)
	                .orElseThrow(() ->
	                        new RuntimeException("User not found"));

	        Map uploadResult = cloudinary.uploader().upload(
	                file.getBytes(),
	                ObjectUtils.asMap(
	                	    "resource_type", "auto"
	                	)
	        );

	        String fileUrl =
	                uploadResult.get("secure_url").toString();
	        
	        String publicId =
	        	    uploadResult.get("public_id").toString();

	        user.setResumeUrl(fileUrl);
	        user.setResumePublicId(publicId);

	        userRepository.save(user);

	        return ResponseEntity.ok(fileUrl);

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity.status(500)
	                .body(e.getMessage());
	    }
	}
	
	@GetMapping("/my-resume")
	public ResponseEntity<?> getMyResume() {

	    try {

	        String email = SecurityContextHolder
	                .getContext()
	                .getAuthentication()
	                .getName();

	        User user = userRepository.findByEmail(email)
	                .orElseThrow(() ->
	                        new RuntimeException("User not found"));

	        String filePath = user.getResumeUrl();

	        if (filePath == null) {
	            return ResponseEntity.badRequest()
	                    .body("No resume uploaded");
	        }

	        File file = new File(filePath);

	        return ResponseEntity.ok(
	                user.getResumeUrl()
	        );

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity.status(500)
	                .body("Failed to get resume");
	    }
	}
	
	@GetMapping("/view")
	public ResponseEntity<?> viewResume(@PathVariable String fileName) {

	    try {
	        Path filePath = Paths.get(System.getProperty("user.dir"), "uploads", fileName);

	        if (!Files.exists(filePath)) {
	            return ResponseEntity
	                    .status(404)
	                    .body("File not found");
	        }

	        Resource resource = new UrlResource(filePath.toUri());

	        String contentType = Files.probeContentType(filePath);
	        if (contentType == null) {
	            contentType = "application/pdf";
	        }

	        return ResponseEntity.ok()
	                .contentType(MediaType.parseMediaType(contentType))
	                .body(resource);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity
	                .status(500)
	                .body("Error loading file: " + e.getMessage());
	    }
	}
	
	@GetMapping("/analyze")
	public ResponseEntity<?> analyzeResume() {

	    try {

	        String email = SecurityContextHolder.getContext()
	                .getAuthentication()
	                .getName();

	        User user = userRepository.findByEmail(email)
	                .orElseThrow(() ->
	                        new RuntimeException("User not found"));

	        String filePath = user.getResumeUrl();

	        if (filePath == null) {

	            return ResponseEntity.badRequest()
	                    .body("Resume not uploaded");
	        }

	        String text =
	                ResumeParser.extractText(filePath);

	        ResumeAnalysisDto analysisDTO =
	                aiService.analyzeResume(text);

	        ObjectMapper mapper =
	                new ObjectMapper();

	        String analysisJson =
	                mapper.writeValueAsString(analysisDTO);

	        // SAVE INSIDE USER
	        user.setResumeAnalysis(analysisJson);

	        userRepository.save(user);

	        // SAVE HISTORY
	        ResumeAnalysis analysis =
	                new ResumeAnalysis();

	        analysis.setEmail(email);

	        analysis.setAnalysisJson(analysisJson);

	        analysis.setAtsScore(
	                analysisDTO.getAtsScore()
	        );

	        analysis.setCreatedAt(
	                LocalDateTime.now()
	        );

	        analysisRepository.save(analysis);

	        return ResponseEntity.ok(
	                analysisDTO
	        );

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity.status(500)
	                .body("AI analysis failed: "
	                        + e.getMessage());
	    }
	}
	
	@GetMapping("/history")
	public ResponseEntity<?> getHistory(HttpServletRequest request) {

	    String authHeader = request.getHeader("Authorization");

	    String token = authHeader.substring(7);

	    String email = jwtUtil.extractUsername(token);

	    return ResponseEntity.ok(
	            analysisRepository.findByEmail(email)
	    );
	}
	
	@DeleteMapping("/history")
	public ResponseEntity<?> deleteHistory() {

	    String email = SecurityContextHolder
	            .getContext()
	            .getAuthentication()
	            .getName();

	    analysisRepository.deleteByEmail(email);

	    return ResponseEntity.ok(
	            "History deleted successfully"
	    );
	}
	
	@PostMapping("/cover-letter")
	public ResponseEntity<?> generateCoverLetter(
	        @RequestBody Map<String, String> body
	) {

	    try {

	        String jobRole = body.get("jobRole");

	        String email = SecurityContextHolder
	                .getContext()
	                .getAuthentication()
	                .getName();

	        User user = userRepository.findByEmail(email)
	                .orElseThrow(() ->
	                        new RuntimeException("User not found"));

	        String filePath = user.getResumeUrl();

	        String resumeText = ResumeParser.extractText(filePath);

	        String prompt = """
	Generate a professional cover letter.

	Job Role:
	%s

	Candidate Resume:
	%s
	
   Rules:
    Do NOT use markdown symbols like # or *.
    
	Return only the cover letter text.
	""".formatted(jobRole, resumeText);

	        String response = aiService.askAI(prompt);

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity
	                .status(500)
	                .body("Failed to generate cover letter");
	    }
	}
	
	@PostMapping("/interview-questions")
	public ResponseEntity<?> generateQuestions(
	        @RequestBody InterviewRequest request
	) {

	    try {

	        String email = SecurityContextHolder
	                .getContext()
	                .getAuthentication()
	                .getName();

	        User user = userRepository
	                .findByEmail(email)
	                .orElseThrow();

	        String resumeText =
	                ResumeParser.extractText(
	                        user.getResumeUrl()
	                );

	        String aiResponse =
	                aiService.generateInterviewQuestions(
	                        resumeText,
	                        request.getRole()
	                );

	        InterviewQuestion question =
	                new InterviewQuestion();

	        question.setEmail(email);

	        question.setQuestions(aiResponse);

	        question.setRole(request.getRole());

	        question.setCreatedAt(LocalDateTime.now());

	        interviewRepository.save(question);

	        return ResponseEntity.ok(aiResponse);

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity
	                .status(500)
	                .body("Failed to generate questions");
	    }
	}
	
	@GetMapping("/recommend-jobs")
	public ResponseEntity<?> recommendJobs() {

	    try {

	        String email =
	                SecurityContextHolder
	                .getContext()
	                .getAuthentication()
	                .getName();

	        User user =
	                userRepository
	                .findByEmail(email)
	                .orElseThrow();

	        String analysis =
	                user.getResumeAnalysis();

	        List<Job> jobs =
	                jobRepository.findAll();

	        ObjectMapper mapper =
	                new ObjectMapper();

	        String jobsData =
	                mapper.writeValueAsString(jobs);

	        RecommendationResponseDTO response =
	                aiService.recommendJobs(
	                        analysis,
	                        jobsData
	                );

	        return ResponseEntity.ok(response);

	    } catch (Exception e) {

	        e.printStackTrace();

	        return ResponseEntity
	                .status(500)
	                .body("Recommendation failed");
	    }
	}
}