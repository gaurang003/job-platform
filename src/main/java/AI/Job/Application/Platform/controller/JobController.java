package AI.Job.Application.Platform.controller;

import AI.Job.Application.Platform.entity.Job;
import AI.Job.Application.Platform.service.JobService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService){
        this.jobService = jobService;
    }

    //Create Job
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Job createJob(@RequestBody Job job){
        return jobService.createJob(job);
    }

    //GET ALL JOBS
    @GetMapping
    public List<Job> getAllJobs(){
        return jobService.getAllJobs();
    }

    //GET JOB BY ID
    @GetMapping("/{id}")
    public Job getJob(@PathVariable Long id){
        return jobService.getJobById(id);
    }

    //UPDATE JOB
    @PutMapping("/{id}")
    public Job updateJob(@PathVariable Long id, @RequestBody Job job){
        return jobService.updateJob(id,job);
    }

    //DELETE JOB
    @DeleteMapping("/{id}")
    public void deleteJob(@PathVariable Long id){
        jobService.deleteJob(id);
    }

}
