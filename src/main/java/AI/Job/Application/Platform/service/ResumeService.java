package AI.Job.Application.Platform.service;

import AI.Job.Application.Platform.entity.Resume;
import AI.Job.Application.Platform.entity.User;
import AI.Job.Application.Platform.repository.ResumeRepository;
import AI.Job.Application.Platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;

    private final String uploadDirectory = "uploads/resumes/";

    public ResumeService(ResumeRepository resumeRepository,
                         UserRepository userRepository) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
    }

    public Resume uploadResume(Long userId, MultipartFile file) throws IOException {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (file.isEmpty()) {
            throw new RuntimeException("Resume file cannot be empty");
        }

        String originalFileName = file.getOriginalFilename();

        String storedFileName = UUID.randomUUID() + "_" + originalFileName;

        Path uploadPath = Paths.get(uploadDirectory);

        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(storedFileName);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        Resume resume = new Resume();

        resume.setUser(user);
        resume.setFileName(originalFileName);
        resume.setFilePath(filePath.toString());
        resume.setUploadedAt(LocalDateTime.now());

        return resumeRepository.save(resume);
    }

    public List<Resume> getUserResumes(Long userId) {

        return resumeRepository.findByUserId(userId);
    }

    public Resume getResumeById(Long id) {

        return resumeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Resume not found"));
    }

    public void deleteResume(Long id) throws IOException {

        Resume resume = getResumeById(id);

        Path filePath = Paths.get(resume.getFilePath());

        Files.deleteIfExists(filePath);

        resumeRepository.delete(resume);
    }
}