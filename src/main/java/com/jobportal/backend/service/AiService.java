package com.jobportal.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobportal.backend.dto.ResumeAnalysisDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.jobportal.backend.dto.RecommendationResponseDTO;

@Service
public class AiService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    public String askAI(String prompt) {

        try {

            String endpoint =
                    "https://openrouter.ai/api/v1/chat/completions";

            RestTemplate restTemplate = new RestTemplate();

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);

            headers.setBearerAuth(apiKey);

            headers.set("HTTP-Referer", "http://localhost:8080");

            headers.set("X-Title", "Job Portal AI");

            String body = """
            {
              "model": "deepseek/deepseek-chat-v3",
              "messages": [
                {
                  "role": "user",
                  "content": "%s"
                }
              ]
            }
            """.formatted(
                    prompt
                            .replace("\"", "\\\"")
                            .replace("\n", "\\n")
            );

            HttpEntity<String> request =
                    new HttpEntity<>(body, headers);

            ResponseEntity<String> response =
                    restTemplate.postForEntity(
                            endpoint,
                            request,
                            String.class
                    );

            ObjectMapper mapper = new ObjectMapper();

            JsonNode root =
                    mapper.readTree(response.getBody());

            return root
                    .path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

        } catch (Exception e) {

            e.printStackTrace();

            return "AI generation failed";
        }
    }
    public ResumeAnalysisDto analyzeResume(String resumeText) {

    	 try {
            String prompt = """
            		Analyze this resume.


            		Format:

            		{
            		  "atsScore": number,
            		  "skills": [],
            		  "missingSkills": [],
            		  "suggestedRoles": [],
            		  "improvements": []
            		}

            		Rules:
            		Return ONLY valid JSON.
                    Do not use markdown.
                    Do not wrap inside ```json
                    Do not explain anything.
                    Return raw JSON only.
                    Resume:
                    """ + resumeText;
            
            String response = askAI(prompt);
            
            response = response
                    .replace("```json", "")
                    .replace("```", "")
                    .trim();

            ObjectMapper mapper = new ObjectMapper();

            return mapper.readValue(
                    response,
                    ResumeAnalysisDto.class
            );

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "AI analysis parsing failed"
            );
        }
    }
    
    
    public String generateInterviewQuestions(
            String resumeText,
            String role
    ) {

        String prompt = """
    Generate interview questions for this candidate.

  Rules:
- Return ONLY questions.
- Do NOT add introductions.
- Do NOT add conclusions.
- Do NOT add explanations.
- Do NOT use markdown symbols like # or *.
- Keep clean plain text format.

- Group by sections:
1.Technical Questions
2.HR Questions
3.Project Questions

Candidate Resume:
%s
""".formatted(role, resumeText);

        return askAI(prompt);
    }
    
    public RecommendationResponseDTO recommendJobs(String resumeAnalysis, String jobsData) {

    	try {
        String prompt = """
        Compare the candidate profile with available jobs.

        Return ONLY valid JSON.

        Format:

        {
          "recommendedJobs": [
            {
              "jobId": 0,
              "title": "",
              "company": "",
              "matchPercentage": 0,
              "matchingSkills": [],
              "missingSkills": [],
              "reason": ""
            }
          ]
        }

          Rules:
          Return ONLY valid JSON.
          Do not use markdown.
          Do not wrap inside ```json
          Do not explain anything.
          Return raw JSON only.
        Candidate Resume Analysis:
        %s

        Available Jobs:
        %s
        """.formatted(resumeAnalysis, jobsData);

        String response = askAI(prompt);

        response = response
                .replace("```json", "")
                .replace("```", "")
                .trim();

        ObjectMapper mapper =
                new ObjectMapper();

        return mapper.readValue(
                response,
                RecommendationResponseDTO.class
        );

    } catch (Exception e) {

        e.printStackTrace();

        throw new RuntimeException(
                "Recommendation parsing failed"
        );
    }
}
}