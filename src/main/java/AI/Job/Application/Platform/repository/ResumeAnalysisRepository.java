package AI.Job.Application.Platform.repository;

import AI.Job.Application.Platform.entity.ResumeAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResumeAnalysisRepository extends JpaRepository<ResumeAnalysis, Long> {

    Optional<ResumeAnalysis> findByResumeId(Long resumeId);

}
