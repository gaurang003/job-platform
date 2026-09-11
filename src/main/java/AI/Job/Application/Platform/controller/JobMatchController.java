package AI.Job.Application.Platform.controller;

import AI.Job.Application.Platform.entity.JobMatch;
import AI.Job.Application.Platform.service.JobMatchService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/job-matches")
public class JobMatchController {

    private final JobMatchService jobMatchService;

    public JobMatchController(
            JobMatchService jobMatchService) {

        this.jobMatchService = jobMatchService;
    }

    @PostMapping("/resume/{resumeId}/job/{jobId}")
    public JobMatch matchResumeWithJob(
            @PathVariable Long resumeId,
            @PathVariable Long jobId) {

        return jobMatchService.matchResumeWithJob(
                resumeId,
                jobId
        );
    }
}