package AI.Job.Application.Platform.service;

import AI.Job.Application.Platform.dto.AIResumeAnalysisRequest;
import AI.Job.Application.Platform.dto.AIResumeAnalysisResponse;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIResumeAnalysisService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    @Value("${ai.api.key}")
    private String apiKey;

    @Value("${ai.api.url}")
    private String apiUrl;

    @Value("${ai.api.model}")
    private String model;

    public AIResumeAnalysisService(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

        this.restClient = RestClient.builder()
                .build();
    }

    public AIResumeAnalysisResponse analyzeResume(
            AIResumeAnalysisRequest request) {

        String prompt = buildPrompt(request);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();

        body.put("model", model);
        body.put("messages", List.of(message));
        body.put("temperature", 0.2);

        String response = restClient.post()
                .uri(apiUrl)
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(String.class);

        return parseResponse(response);
    }

    private String buildPrompt(AIResumeAnalysisRequest request) {

        return """
                You are an expert technical recruiter and resume analyzer.

                Analyze the candidate's resume against the job description.

                IMPORTANT:
                - Only use information present in the resume.
                - Do not invent skills or experience.
                - Distinguish professional experience from learning/upskilling.
                - Compare the actual resume content with the job requirements.
                - Return ONLY valid JSON.
                - Do not use markdown.
                - matchScore must be between 0 and 100.

                Required JSON format:

                {
                  "matchScore": 85,
                  "matchedSkills": ["Java", "Spring Boot"],
                  "missingSkills": ["Kafka"],
                  "strengths": [
                    "Strong Spring Boot backend experience"
                  ],
                  "weaknesses": [
                    "Limited Kafka experience"
                  ],
                  "recommendation": "Strong Match",
                  "explanation": "The candidate is a strong match because..."
                }

                JOB TITLE:
                %s

                JOB DESCRIPTION:
                %s

                REQUIRED SKILLS:
                %s

                REQUIRED EXPERIENCE:
                %s years

                CANDIDATE RESUME:
                %s
                """.formatted(
                safe(request.getJobTitle()),
                safe(request.getJobDescription()),
                safe(request.getJobSkills()),
                request.getExperienceRequired(),
                safe(request.getResumeText())
        );
    }

    private AIResumeAnalysisResponse parseResponse(String response) {

        try {

            JsonNode root = objectMapper.readTree(response);

            String content = root
                    .path("choices")
                    .path(0)
                    .path("message")
                    .path("content")
                    .asText();

            if (content == null || content.isBlank()) {
                throw new RuntimeException(
                        "AI returned an empty response");
            }

            content = cleanJson(content);

            JsonNode aiJson = objectMapper.readTree(content);

            AIResumeAnalysisResponse result =
                    new AIResumeAnalysisResponse();

            result.setMatchScore(
                    aiJson.path("matchScore").asDouble()
            );

            result.setMatchedSkills(
                    readStringList(
                            aiJson.path("matchedSkills")
                    )
            );

            result.setMissingSkills(
                    readStringList(
                            aiJson.path("missingSkills")
                    )
            );

            result.setStrengths(
                    readStringList(
                            aiJson.path("strengths")
                    )
            );

            result.setWeaknesses(
                    readStringList(
                            aiJson.path("weaknesses")
                    )
            );

            result.setRecommendation(
                    aiJson.path("recommendation").asText()
            );

            result.setExplanation(
                    aiJson.path("explanation").asText()
            );

            return result;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse AI response: "
                            + e.getMessage(),
                    e
            );
        }
    }

    private List<String> readStringList(JsonNode node) {

        List<String> result = new ArrayList<>();

        if (node != null && node.isArray()) {

            for (JsonNode item : node) {
                result.add(item.asText());
            }
        }

        return result;
    }

    private String cleanJson(String content) {

        content = content.trim();

        if (content.startsWith("```json")) {
            content = content.substring(7);
        } else if (content.startsWith("```")) {
            content = content.substring(3);
        }

        if (content.endsWith("```")) {
            content = content.substring(
                    0,
                    content.length() - 3
            );
        }

        return content.trim();
    }

    private String safe(String value) {

        return value == null ? "" : value;
    }
}