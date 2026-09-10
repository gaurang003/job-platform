package AI.Job.Application.Platform.controller;

import AI.Job.Application.Platform.entity.ResumeAnalysis;
import AI.Job.Application.Platform.service.ResumeAnalysisService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/resume-analysis")
public class ResumeAnalysisController {

    private final ResumeAnalysisService resumeAnalysisService;

    public ResumeAnalysisController(ResumeAnalysisService resumeAnalysisService){
        this.resumeAnalysisService = resumeAnalysisService;
    }

    @PostMapping("/resume/{resumeId}")
    public ResumeAnalysis createAnalysis(@PathVariable Long resumeId){
        return resumeAnalysisService.createAnalysis(resumeId);
    }

    @GetMapping("/resume/{resumeId}")
    public ResumeAnalysis getAnalysis(@PathVariable Long resumeId){
        return resumeAnalysisService.getAnalysis(resumeId);
    }

}
