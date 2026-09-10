package AI.Job.Application.Platform.service;

import AI.Job.Application.Platform.entity.Resume;
import AI.Job.Application.Platform.entity.ResumeAnalysis;
import AI.Job.Application.Platform.repository.ResumeAnalysisRepository;
import AI.Job.Application.Platform.repository.ResumeRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ResumeAnalysisService {

    private final ResumeRepository resumeRepository;
    private final ResumeAnalysisRepository resumeAnalysisRepository;

    public ResumeAnalysisService(
            ResumeRepository resumeRepository,
            ResumeAnalysisRepository resumeAnalysisRepository) {

        this.resumeRepository = resumeRepository;
        this.resumeAnalysisRepository = resumeAnalysisRepository;
    }

    public ResumeAnalysis createAnalysis(Long resumeId) {

        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new RuntimeException("Resume not found"));

        String resumeText = resume.getExtractedText();

        if (resumeText == null || resumeText.isBlank()) {
            throw new RuntimeException(
                    "Resume does not contain extracted text"
            );
        }

        ResumeAnalysis analysis =
                resumeAnalysisRepository.findByResumeId(resumeId)
                        .orElse(new ResumeAnalysis());

        analysis.setResume(resume);

        // Extract different sections
        String skills = extractSection(
                resumeText,
                "skills",
                "technical skills",
                "education",
                "experience",
                "work experience",
                "projects"
        );

        String education = extractSection(
                resumeText,
                "education",
                "academic",
                "experience",
                "work experience",
                "projects",
                "skills"
        );

        String experience = extractSection(
                resumeText,
                "experience",
                "work experience",
                "professional experience",
                "projects",
                "education",
                "skills"
        );

        String projects = extractSection(
                resumeText,
                "projects",
                "project",
                "education",
                "experience",
                "skills"
        );

        String keywords = extractKeywords(resumeText);

        analysis.setSkills(skills);
        analysis.setEducation(education);
        analysis.setExperience(experience);
        analysis.setProjects(projects);
        analysis.setKeywords(keywords);

        analysis.setAnalyzedAt(LocalDateTime.now());

        return resumeAnalysisRepository.save(analysis);
    }

    public ResumeAnalysis getAnalysis(Long resumeId) {

        return resumeAnalysisRepository.findByResumeId(resumeId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Resume analysis not found"
                        )
                );
    }

    private String extractSection(
            String text,
            String... sectionNames) {

        String[] lines = text.split("\\r?\\n");

        StringBuilder section = new StringBuilder();

        boolean foundSection = false;

        Set<String> names = Arrays.stream(sectionNames)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        for (String line : lines) {

            String cleanedLine = line
                    .trim()
                    .toLowerCase();

            if (cleanedLine.isEmpty()) {
                continue;
            }

            if (!foundSection && names.contains(cleanedLine)) {
                foundSection = true;
                continue;
            }

            if (foundSection) {

                if (isAnotherSectionHeader(cleanedLine, names)) {
                    break;
                }

                section.append(line.trim())
                        .append("\n");
            }
        }

        if (section.isEmpty()) {
            return "Section not detected";
        }

        return section.toString().trim();
    }

    private boolean isAnotherSectionHeader(
            String line,
            Set<String> currentSectionNames) {

        Set<String> allHeaders = new HashSet<>(Arrays.asList(
                "skills",
                "technical skills",
                "education",
                "academic",
                "experience",
                "work experience",
                "professional experience",
                "projects",
                "project",
                "certifications",
                "achievements",
                "summary",
                "profile"
        ));

        return allHeaders.contains(line)
                && !currentSectionNames.contains(line);
    }

    private String extractKeywords(String text) {

        String lowerText = text.toLowerCase();

        Set<String> technologyKeywords = new HashSet<>(Arrays.asList(
                "java",
                "spring boot",
                "spring",
                "spring mvc",
                "spring security",
                "hibernate",
                "jpa",
                "rest api",
                "rest",
                "microservices",
                "sql",
                "mysql",
                "postgresql",
                "mongodb",
                "docker",
                "aws",
                "git",
                "github",
                "maven",
                "gradle",
                "html",
                "css",
                "javascript",
                "react",
                "angular",
                "python",
                "c++",
                "c#",
                ".net"
        ));

        return technologyKeywords.stream()
                .filter(lowerText::contains)
                .collect(Collectors.joining(", "));
    }
}