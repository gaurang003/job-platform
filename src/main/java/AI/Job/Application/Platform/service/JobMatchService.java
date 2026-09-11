package AI.Job.Application.Platform.service;

import AI.Job.Application.Platform.entity.Job;
import AI.Job.Application.Platform.entity.JobMatch;
import AI.Job.Application.Platform.entity.Resume;
import AI.Job.Application.Platform.repository.JobMatchRepository;
import AI.Job.Application.Platform.repository.JobRepository;
import AI.Job.Application.Platform.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobMatchService {

    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final JobMatchRepository jobMatchRepository;

    public JobMatchService(
            ResumeRepository resumeRepository,
            JobRepository jobRepository,
            JobMatchRepository jobMatchRepository) {

        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.jobMatchRepository = jobMatchRepository;
    }

    public JobMatch matchResumeWithJob(
            Long resumeId,
            Long jobId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        String resumeText = resume.getExtractedText();

        if (resumeText == null || resumeText.isBlank()) {
            throw new RuntimeException(
                    "Resume does not contain extracted text"
            );
        }

        String jobSkills = job.getSkills();

        if (jobSkills == null || jobSkills.isBlank()) {
            throw new RuntimeException(
                    "Job does not contain required skills"
            );
        }

        // ------------------------------------------------
        // 1. SKILL MATCH
        // ------------------------------------------------

        Set<String> resumeSkills =
                extractSkills(resumeText);

        Set<String> requiredSkills =
                extractSkills(jobSkills);

        Set<String> matchedSkills =
                new HashSet<>(resumeSkills);

        matchedSkills.retainAll(requiredSkills);

        Set<String> missingSkills =
                new HashSet<>(requiredSkills);

        missingSkills.removeAll(resumeSkills);

        double skillMatchScore = 0.0;

        if (!requiredSkills.isEmpty()) {

            skillMatchScore =
                    ((double) matchedSkills.size()
                            / requiredSkills.size())
                            * 100;
        }

        // ------------------------------------------------
        // 2. EXPERIENCE MATCH
        // ------------------------------------------------

        double experienceMatchScore =
                calculateExperienceScore(
                        resumeText,
                        job.getExperienceRequired()
                );

        // ------------------------------------------------
        // 3. EDUCATION MATCH
        // ------------------------------------------------

        double educationMatchScore =
                calculateEducationScore(resumeText);

        // ------------------------------------------------
        // 4. OVERALL SCORE
        // ------------------------------------------------

        double overallScore =
                (skillMatchScore * 0.60)
                        + (experienceMatchScore * 0.25)
                        + (educationMatchScore * 0.15);

        String recommendation =  generateRecommendation(overallScore);

        // ------------------------------------------------
        // 5. Find existing match
        // ------------------------------------------------

        List<JobMatch> existingMatches =
                jobMatchRepository.findByResumeId(resumeId)
                        .stream()
                        .filter(match ->
                                match.getJob()
                                        .getId()
                                        .equals(jobId))
                        .toList();

        JobMatch jobMatch;

        if (!existingMatches.isEmpty()) {

            jobMatch = existingMatches.get(0);

        } else {

            jobMatch = new JobMatch();
        }

        // ------------------------------------------------
        // 6. Save result
        // ------------------------------------------------

        jobMatch.setResume(resume);
        jobMatch.setJob(job);

        jobMatch.setSkillMatchScore(
                round(skillMatchScore)
        );

        jobMatch.setExperienceMatchScore(
                round(experienceMatchScore)
        );

        jobMatch.setEducationMatchScore(
                round(educationMatchScore)
        );

        jobMatch.setMatchScore(
                round(overallScore)
        );

        jobMatch.setMatchedSkills(
                matchedSkills.stream()
                        .sorted()
                        .collect(Collectors.joining(", "))
        );

        jobMatch.setMissingSkills(
                missingSkills.stream()
                        .sorted()
                        .collect(Collectors.joining(", "))
        );

        jobMatch.setMatchedAt(
                LocalDateTime.now()
        );

        jobMatch.setRecommendation(recommendation);

        jobMatch.setExplanation(
                generateExplanation(
                        overallScore,
                        matchedSkills,
                        missingSkills,
                        experienceMatchScore,
                        educationMatchScore
                )
        );

        return jobMatchRepository.save(jobMatch);


    }

    // ------------------------------------------------
    // SKILL EXTRACTION
    // ------------------------------------------------

    private Set<String> extractSkills(String text) {

        String lowerText = text.toLowerCase();

        Set<String> skills = new HashSet<>();

        List<String> knownSkills = Arrays.asList(
                "spring boot",
                "spring mvc",
                "spring security",
                "rest api",
                "microservices",
                "hibernate",
                "jpa",
                "mysql",
                "postgresql",
                "mongodb",
                "docker",
                "aws",
                "github",
                "maven",
                "gradle",
                "javascript",
                "react",
                "angular",
                "python",
                "thymeleaf",
                "jdbc",
                "kafka",
                "redis",
                "java",
                "sql",
                "git"
        );

        for (String skill : knownSkills) {

            if (lowerText.contains(skill.toLowerCase())) {
                skills.add(skill);
            }
        }

        return skills;
    }

    // ------------------------------------------------
    // EXPERIENCE CALCULATION
    // ------------------------------------------------

    private double calculateExperienceScore(
            String resumeText,
            Integer requiredExperience) {

        if (requiredExperience == null ||
                requiredExperience <= 0) {

            return 100.0;
        }

        String lowerText = resumeText.toLowerCase();

        double resumeExperience = 0.0;

        if (lowerText.contains("1.5+ years")) {
            resumeExperience = 1.5;
        } else if (lowerText.contains("1.5 years")) {
            resumeExperience = 1.5;
        } else if (lowerText.contains("1+ years")) {
            resumeExperience = 1.0;
        } else if (lowerText.contains("2+ years")) {
            resumeExperience = 2.0;
        } else if (lowerText.contains("2 years")) {
            resumeExperience = 2.0;
        } else if (lowerText.contains("3+ years")) {
            resumeExperience = 3.0;
        } else if (lowerText.contains("3 years")) {
            resumeExperience = 3.0;
        }

        if (resumeExperience >= requiredExperience) {
            return 100.0;
        }

        if (resumeExperience <= 0) {
            return 0.0;
        }

        return Math.min(
                (resumeExperience / requiredExperience) * 100,
                100
        );
    }

    // ------------------------------------------------
    // EDUCATION CALCULATION
    // ------------------------------------------------

    private double calculateEducationScore(
            String resumeText) {

        String lowerText = resumeText.toLowerCase();

        if (lowerText.contains("b.sc")
                || lowerText.contains("bsc")
                || lowerText.contains("bachelor")
                || lowerText.contains("b.tech")
                || lowerText.contains("btech")
                || lowerText.contains("m.sc")
                || lowerText.contains("msc")
                || lowerText.contains("master")) {

            return 100.0;
        }

        return 50.0;
    }

    // ------------------------------------------------
    // ROUNDING
    // ------------------------------------------------

    private double round(double value) {

        return Math.round(value * 100.0) / 100.0;
    }

    private String generateRecommendation(double score) {

        if (score >= 85) {
            return "Strong Match";
        }

        if (score >= 70) {
            return "Good Match";
        }

        if (score >= 50) {
            return "Moderate Match";
        }

        return "Low Match";
    }

    private String generateExplanation(
            double score,
            Set<String> matchedSkills,
            Set<String> missingSkills,
            double experienceScore,
            double educationScore) {

        StringBuilder explanation =
                new StringBuilder();

        explanation.append(
                "The candidate has a "
                        + round(score)
                        + "% overall match for this job. "
        );

        if (!matchedSkills.isEmpty()) {

            explanation.append(
                    "Matching skills include: "
                            + matchedSkills.stream()
                            .sorted()
                            .collect(Collectors.joining(", "))
                            + ". "
            );
        }

        if (!missingSkills.isEmpty()) {

            explanation.append(
                    "Missing or unmatched skills include: "
                            + missingSkills.stream()
                            .sorted()
                            .collect(Collectors.joining(", "))
                            + ". "
            );
        } else {

            explanation.append(
                    "All listed job skills were found in the resume. "
            );
        }

        if (experienceScore >= 100) {

            explanation.append(
                    "The candidate meets the required experience level. "
            );

        } else {

            explanation.append(
                    "The candidate may have less experience "
                            + "than required by the job. "
            );
        }

        if (educationScore >= 100) {

            explanation.append(
                    "The resume contains relevant higher-education information."
            );
        }

        return explanation.toString();
    }
}