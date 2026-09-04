package AI.Job.Application.Platform.controller;

import AI.Job.Application.Platform.entity.Application;
import AI.Job.Application.Platform.service.ApplicationService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(
            ApplicationService applicationService) {

        this.applicationService = applicationService;
    }

    // APPLY FOR JOB
    @PostMapping("/apply")
    @ResponseStatus(HttpStatus.CREATED)
    public Application applyForJob(
            @RequestParam Long userId,
            @RequestParam Long jobId) {

        return applicationService
                .applyForJob(userId, jobId);
    }

    // GET ALL APPLICATIONS
    @GetMapping
    public List<Application> getAllApplications() {

        return applicationService
                .getAllApplications();
    }

    // GET APPLICATION BY ID
    @GetMapping("/{id}")
    public Application getApplication(
            @PathVariable Long id) {

        return applicationService
                .getApplication(id);
    }

    // GET APPLICATIONS BY USER
    @GetMapping("/user/{userId}")
    public List<Application> getByUser(
            @PathVariable Long userId) {

        return applicationService
                .getApplicationsByUser(userId);
    }

    // GET APPLICATIONS BY JOB
    @GetMapping("/job/{jobId}")
    public List<Application> getByJob(
            @PathVariable Long jobId) {

        return applicationService
                .getApplicationsByJob(jobId);
    }

    // UPDATE STATUS
    @PutMapping("/{id}/status")
    public Application updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return applicationService
                .updateStatus(id, status);
    }

    // DELETE
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @PathVariable Long id) {

        applicationService
                .deleteApplication(id);
    }
}