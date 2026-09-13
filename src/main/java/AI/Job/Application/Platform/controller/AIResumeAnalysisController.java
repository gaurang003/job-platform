package AI.Job.Application.Platform.controller;

import AI.Job.Application.Platform.dto.AIResumeAnalysisRequest;
import AI.Job.Application.Platform.dto.AIResumeAnalysisResponse;
import AI.Job.Application.Platform.entity.AIJobAnalysis;
import AI.Job.Application.Platform.entity.Job;
import AI.Job.Application.Platform.entity.Resume;
import AI.Job.Application.Platform.repository.AIJobAnalysisRepository;
import AI.Job.Application.Platform.repository.JobRepository;
import AI.Job.Application.Platform.repository.ResumeRepository;
import AI.Job.Application.Platform.service.AIResumeAnalysisService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/ai-analysis")
public class AIResumeAnalysisController {

    private final AIResumeAnalysisService aiResumeAnalysisService;
    private final ResumeRepository resumeRepository;
    private final JobRepository jobRepository;
    private final AIJobAnalysisRepository aiJobAnalysisRepository;

    public AIResumeAnalysisController(
            AIResumeAnalysisService aiResumeAnalysisService,
            ResumeRepository resumeRepository,
            JobRepository jobRepository,
            AIJobAnalysisRepository aiJobAnalysisRepository) {

        this.aiResumeAnalysisService = aiResumeAnalysisService;
        this.resumeRepository = resumeRepository;
        this.jobRepository = jobRepository;
        this.aiJobAnalysisRepository = aiJobAnalysisRepository;
    }

    @PostMapping("/resume/{resumeId}/job/{jobId}")
    public AIResumeAnalysisResponse analyzeResumeAgainstJob(
            @PathVariable Long resumeId,
            @PathVariable Long jobId) {

        // 1. Get Resume
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        // 2. Get Job
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        // 3. Validate extracted resume text
        if (resume.getExtractedText() == null
                || resume.getExtractedText().isBlank()) {

            throw new RuntimeException(
                    "Resume does not contain extracted text");
        }

        // 4. Create AI request
        AIResumeAnalysisRequest request =
                new AIResumeAnalysisRequest();

        request.setResumeText(resume.getExtractedText());
        request.setJobTitle(job.getTitle());
        request.setJobDescription(job.getDescription());
        request.setJobSkills(job.getSkills());
        request.setExperienceRequired(job.getExperienceRequired());

        // 5. Call Gemini
        AIResumeAnalysisResponse aiResponse =
                aiResumeAnalysisService.analyzeResume(request);

        // 6. Check whether analysis already exists
        AIJobAnalysis analysis =
                aiJobAnalysisRepository
                        .findByResumeIdAndJobId(resumeId, jobId)
                        .orElseGet(AIJobAnalysis::new);

        // 7. Set Resume and Job
        analysis.setResume(resume);
        analysis.setJob(job);

        // 8. Set AI result
        analysis.setMatchScore(
                aiResponse.getMatchScore()
        );

        analysis.setMatchedSkills(
                aiResponse.getMatchedSkills() == null
                        ? ""
                        : String.join(", ",
                        aiResponse.getMatchedSkills())
        );

        analysis.setMissingSkills(
                aiResponse.getMissingSkills() == null
                        ? ""
                        : String.join(", ",
                        aiResponse.getMissingSkills())
        );

        analysis.setStrengths(
                aiResponse.getStrengths() == null
                        ? ""
                        : String.join(", ",
                        aiResponse.getStrengths())
        );

        analysis.setWeaknesses(
                aiResponse.getWeaknesses() == null
                        ? ""
                        : String.join(", ",
                        aiResponse.getWeaknesses())
        );

        analysis.setRecommendation(
                aiResponse.getRecommendation()
        );

        analysis.setExplanation(
                aiResponse.getExplanation()
        );

        // 9. Set analysis time
        analysis.setAnalyzedAt(
                LocalDateTime.now()
        );

        // 10. Save to database
        aiJobAnalysisRepository.save(analysis);

        // 11. Return AI response to Postman
        return aiResponse;
    }

    @GetMapping("/resume/{resumeId}/job/{jobId}")
    public AIJobAnalysis getAnalysis(
            @PathVariable Long resumeId,
            @PathVariable Long jobId) {

        return aiJobAnalysisRepository
                .findByResumeIdAndJobId(resumeId, jobId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "AI analysis not found for this resume and job"
                        ));
    }
}