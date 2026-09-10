package AI.Job.Application.Platform.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "resume_analysis")
public class ResumeAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resume_id", nullable = false, unique = true)
    private Resume resume;

    @Column(columnDefinition = "LONGTEXT")
    private String skills;

    @Column(columnDefinition = "LONGTEXT")
    private String education;

    @Column(columnDefinition = "LONGTEXT")
    private String experience;

    @Column(columnDefinition = "LONGTEXT")
    private String projects;

    @Column(columnDefinition = "LONGTEXT")
    private String keywords;

    @Column(nullable = false)
    private LocalDateTime analyzedAt;

    public ResumeAnalysis() {
    }

    public Long getId() {
        return id;
    }

    public Resume getResume() {
        return resume;
    }

    public String getSkills() {
        return skills;
    }

    public String getEducation() {
        return education;
    }

    public String getExperience() {
        return experience;
    }

    public String getProjects() {
        return projects;
    }

    public String getKeywords() {
        return keywords;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setResume(Resume resume) {
        this.resume = resume;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public void setProjects(String projects) {
        this.projects = projects;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}