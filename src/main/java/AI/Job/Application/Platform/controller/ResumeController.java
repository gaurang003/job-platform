package AI.Job.Application.Platform.controller;

import AI.Job.Application.Platform.entity.Resume;
import AI.Job.Application.Platform.service.ResumeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/resumes")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping(value = "/user/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Resume uploadResume(@PathVariable Long userId, @RequestParam("file") MultipartFile file) throws IOException {

        return resumeService.uploadResume(userId, file);
    }

    @GetMapping("/user/{userId}")
    public List<Resume> getUserResumes(@PathVariable Long userId) {

        return resumeService.getUserResumes(userId);
    }

    @GetMapping("/{id}")
    public Resume getResume(@PathVariable Long id) {

        return resumeService.getResumeById(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteResume(@PathVariable Long id) throws IOException {

        resumeService.deleteResume(id);
    }
}