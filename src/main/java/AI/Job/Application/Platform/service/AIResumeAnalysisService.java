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

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Value("${gemini.api.model}")
    private String model;

    public AIResumeAnalysisService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = RestClient.builder().build();
    }

    public AIResumeAnalysisResponse analyzeResume(
            AIResumeAnalysisRequest request) {

        String prompt = buildPrompt(request);

        Map<String, Object> textPart = new HashMap<>();
        textPart.put("text", prompt);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(textPart));

        Map<String, Object> body = new HashMap<>();
        body.put("contents", List.of(content));

        int maxAttempts = 3;

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {

            try {

                String response = restClient.post()
                        .uri(apiUrl)
                        .header("x-goog-api-key", apiKey)
                        .header(
                                HttpHeaders.CONTENT_TYPE,
                                MediaType.APPLICATION_JSON_VALUE
                        )
                        .body(body)
                        .retrieve()
                        .body(String.class);

                return parseResponse(response);

            } catch (org.springframework.web.client.HttpServerErrorException.ServiceUnavailable e) {

                if (attempt == maxAttempts) {

                    throw new RuntimeException(
                            "Gemini service is temporarily unavailable after "
                                    + maxAttempts
                                    + " attempts. Please try again later.",
                            e
                    );
                }

                try {
                    Thread.sleep(1000L * attempt);
                } catch (InterruptedException interruptedException) {
                    Thread.currentThread().interrupt();

                    throw new RuntimeException(
                            "Gemini retry was interrupted.",
                            interruptedException
                    );
                }
            }
        }

        throw new RuntimeException("Gemini analysis failed.");
    }

    private String buildPrompt(AIResumeAnalysisRequest request) {

        return """
                You are an expert technical recruiter and resume analyzer.

                Analyze the candidate's resume against the job description.

                IMPORTANT RULES:

                1. Only use information actually present in the resume.
                2. Do not invent skills, experience, education, or projects.
                3. Distinguish professional experience from learning/upskilling.
                4. Compare the candidate's actual experience with the job requirements.
                5. A skill mentioned under "Currently Upskilling", "Learning",
                   "Familiarity", or similar sections should NOT be treated
                   as professional experience.
                6. matchScore must be between 0 and 100.
                7. Return ONLY valid JSON.
                8. Do not use markdown.
                9. Do not wrap the JSON inside ```json.
                10. matchedSkills should contain skills that genuinely match
                    the job requirements.
                11. missingSkills should contain important job requirements
                    that are not demonstrated in the resume.
                12. Give realistic recommendations.

                Required JSON format:

                {
                  "matchScore": 85,
                  "matchedSkills": [
                    "Java",
                    "Spring Boot",
                    "SQL"
                  ],
                  "missingSkills": [
                    "Kafka"
                  ],
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

            /*
             * Gemini response structure:
             *
             * candidates
             *   -> 0
             *      -> content
             *         -> parts
             *            -> 0
             *               -> text
             */

            String content = root
                    .path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            if (content == null || content.isBlank()) {
                throw new RuntimeException(
                        "Gemini returned an empty response"
                );
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
                    "Failed to parse Gemini response: "
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