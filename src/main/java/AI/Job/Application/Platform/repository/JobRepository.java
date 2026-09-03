package AI.Job.Application.Platform.repository;

import AI.Job.Application.Platform.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {


}