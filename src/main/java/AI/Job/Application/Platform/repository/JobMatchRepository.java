package AI.Job.Application.Platform.repository;

import AI.Job.Application.Platform.entity.JobMatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobMatchRepository extends JpaRepository<JobMatch, Long> {

    List<JobMatch> findByResumeId(Long resumeId);

    List<JobMatch> findByJobId(Long jobId);

}
