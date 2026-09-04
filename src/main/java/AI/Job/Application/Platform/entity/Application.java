package AI.Job.Application.Platform.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Candidate who applied
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Job applied for
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    private String status;

    private LocalDateTime appliedAt;

    public Application() {
    }

    @PrePersist
    public void onCreate() {
        appliedAt = LocalDateTime.now();

        if (status == null) {
            status = "PENDING";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}