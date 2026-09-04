package AI.Job.Application.Platform.service;


import AI.Job.Application.Platform.entity.Application;
import AI.Job.Application.Platform.entity.Job;
import AI.Job.Application.Platform.entity.User;
import AI.Job.Application.Platform.repository.ApplicationRepository;
import AI.Job.Application.Platform.repository.JobRepository;
import AI.Job.Application.Platform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    public ApplicationService(
            ApplicationRepository applicationRepository,
            UserRepository userRepository,
            JobRepository jobRepository){

        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    // APPLY FOR JOB
    public Application applyForJob(Long userId,Long jobId){

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new RuntimeException("Job not found"));

        if(applicationRepository
                .existsByUserIdAndJobId(userId,jobId)){
            throw new RuntimeException("User has already applied for this job");
        }

        Application application = new Application();
        application.setUser(user);
        application.setJob(job);

        return applicationRepository.save(application);

    }

    //GET APPLICATION BY ID
    public Application getApplication(Long id){
        return applicationRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Application not found"));
    }

    //GET ALL APPLICATIONS
    public List<Application> getAllApplications(){
        return  applicationRepository.findAll();
    }

    //GET USER APPLICATIONS
    public List<Application> getApplicationsByUser(Long userId){
        return applicationRepository.findByUserId(userId);
    }

    //GET JOBS APPLICATIONS
    public List<Application> getApplicationsByJob(Long jobId){
        return applicationRepository.findByJobId(jobId);
    }

    //UPDATE APPLICATION STATUS
    public Application updateStatus(Long applicationId, String status){
        Application application = getApplication(applicationId);
        application.setStatus(status);

        return applicationRepository.save(application);
    }

    //DELETE APPLICATION
    public void deleteApplication(Long id){
        Application application = getApplication(id);

        applicationRepository.delete(application);
    }
}












