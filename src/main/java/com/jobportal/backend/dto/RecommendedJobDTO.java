package com.jobportal.backend.dto;

import java.util.List;

public class RecommendedJobDTO {
	
	private Long jobId;

    private String title;

    private String company;

    private int matchPercentage;

    private List<String> matchingSkills;

    private List<String> missingSkills;

    private String reason;

	public RecommendedJobDTO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RecommendedJobDTO(Long jobId, String title, String company, int matchPercentage, List<String> matchingSkills,
			List<String> missingSkills, String reason) {
		super();
		this.jobId = jobId;
		this.title = title;
		this.company = company;
		this.matchPercentage = matchPercentage;
		this.matchingSkills = matchingSkills;
		this.missingSkills = missingSkills;
		this.reason = reason;
	}

	public Long getJobId() {
		return jobId;
	}

	public void setJobId(Long jobId) {
		this.jobId = jobId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public int getMatchPercentage() {
		return matchPercentage;
	}

	public void setMatchPercentage(int matchPercentage) {
		this.matchPercentage = matchPercentage;
	}

	public List<String> getMatchingSkills() {
		return matchingSkills;
	}

	public void setMatchingSkills(List<String> matchingSkills) {
		this.matchingSkills = matchingSkills;
	}

	public List<String> getMissingSkills() {
		return missingSkills;
	}

	public void setMissingSkills(List<String> missingSkills) {
		this.missingSkills = missingSkills;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

   
}