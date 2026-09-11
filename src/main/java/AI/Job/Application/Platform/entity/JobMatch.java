package AI.Job.Application.Platform.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "job_matches")
public class JobMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false)
    private Resume resume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @Column(nullable = false)
    private Double matchScore;

    @Column(nullable = false)
    private Double skillMatchScore;

    @Column(nullable = false)
    private Double experienceMatchScore;

    @Column(nullable = false)
    private Double educationMatchScore;

    @Column(columnDefinition = "LONGTEXT")
    private String matchedSkills;

    @Column(columnDefinition = "LONGTEXT")
    private String missingSkills;

    @Column(columnDefinition = "TEXT")
    private String recommendation;

    @Column(columnDefinition = "LONGTEXT")
    private String explanation;

    @Column(nullable = false)
    private LocalDateTime matchedAt;

    public JobMatch() {
    }

    public Long getId() {
        return id;
    }

    public Resume getResume() {
        return resume;
    }

    public Job getJob() {
        return job;
    }

    public Double getMatchScore() {
        return matchScore;
    }

    public Double getSkillMatchScore() {
        return skillMatchScore;
    }

    public Double getExperienceMatchScore() {
        return experienceMatchScore;
    }

    public Double getEducationMatchScore() {
        return educationMatchScore;
    }

    public String getMatchedSkills() {
        return matchedSkills;
    }

    public String getMissingSkills() {
        return missingSkills;
    }

    public LocalDateTime getMatchedAt() {
        return matchedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public void setMatchScore(Double matchScore) {
        this.matchScore = matchScore;
    }

    public void setSkillMatchScore(Double skillMatchScore) {
        this.skillMatchScore = skillMatchScore;
    }

    public void setExperienceMatchScore(Double experienceMatchScore) {
        this.experienceMatchScore = experienceMatchScore;
    }

    public void setEducationMatchScore(Double educationMatchScore) {
        this.educationMatchScore = educationMatchScore;
    }

    public void setMatchedSkills(String matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public void setMissingSkills(String missingSkills) {
        this.missingSkills = missingSkills;
    }

    public void setMatchedAt(LocalDateTime matchedAt) {
        this.matchedAt = matchedAt;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}