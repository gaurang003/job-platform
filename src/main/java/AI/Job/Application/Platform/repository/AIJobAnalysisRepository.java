package AI.Job.Application.Platform.repository;

import AI.Job.Application.Platform.entity.AIJobAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AIJobAnalysisRepository extends JpaRepository<AIJobAnalysis, Long> {

    Optional<AIJobAnalysis> findByResumeIdAndJobId(
            Long resumeId,
            Long jobId
    );

    List<AIJobAnalysis> findByResumeId(Long resumeId);

    List<AIJobAnalysis> findByJobId(Long jobId);
}