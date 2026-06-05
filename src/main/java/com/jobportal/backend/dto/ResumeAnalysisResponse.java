package com.jobportal.backend.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumeAnalysisResponse {
	private int score;
	private String skills;
	private String suggestions;
}