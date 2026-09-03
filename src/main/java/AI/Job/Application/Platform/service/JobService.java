package AI.Job.Application.Platform.service;

import AI.Job.Application.Platform.entity.Job;
import AI.Job.Application.Platform.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JobService {

    private final JobRepository jobRepository;


    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    //Create
    public Job createJob(Job job){
        return jobRepository.save(job);
    }

    //READ ALL
    public List<Job> getAllJobs(){
        return jobRepository.findAll();
    }

    //READ BY ID
    public Job getJobById(Long id){
        return jobRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Job not found with id: " + id));
    }

    //UPDATE
    public Job updateJob(Long id, Job updateJob){
        Job existingJob = getJobById(id);

        existingJob.setTitle(updateJob.getTitle());
        existingJob.setDescription(updateJob.getDescription());
        existingJob.setCompanyName(updateJob.getCompanyName());
        existingJob.setLocation(updateJob.getLocation());
        existingJob.setJobType(updateJob.getJobType());
        existingJob.setExperienceRequired(updateJob.getExperienceRequired());
        existingJob.setSalary(updateJob.getSalary());
        existingJob.setSkills(updateJob.getSkills());

        return jobRepository.save(existingJob);
    }

    //DELETE
    public void deleteJob(Long id){
        Job job = getJobById(id);
        jobRepository.delete(job);
    }
}
