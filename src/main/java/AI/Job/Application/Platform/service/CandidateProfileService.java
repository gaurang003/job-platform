package AI.Job.Application.Platform.service;

import AI.Job.Application.Platform.dto.CandidateProfileRequest;
import AI.Job.Application.Platform.entity.CandidateProfile;
import AI.Job.Application.Platform.entity.User;
import AI.Job.Application.Platform.repository.CandidateProfileRepository;
import AI.Job.Application.Platform.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CandidateProfileService {

    private final CandidateProfileRepository profileRepository;
    private final UserRepository userRepository;

    public CandidateProfileService(
            CandidateProfileRepository profileRepository,
            UserRepository userRepository) {

        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    // CREATE PROFILE
    public CandidateProfile createProfile(
            Long userId,
            CandidateProfileRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Optional<CandidateProfile> existingProfile =
                profileRepository.findByUserId(userId);

        if (existingProfile.isPresent()) {
            throw new RuntimeException(
                    "Candidate profile already exists");
        }

        CandidateProfile profile = new CandidateProfile();

        profile.setUser(user);
        profile.setHeadline(request.getHeadline());
        profile.setSummary(request.getSummary());
        profile.setSkills(request.getSkills());
        profile.setEducation(request.getEducation());
        profile.setExperienceYears(
                request.getExperienceYears()
        );

        return profileRepository.save(profile);
    }

    // GET PROFILE BY USER
    public CandidateProfile getProfileByUser(Long userId) {

        return profileRepository.findByUserId(userId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Candidate profile not found"));
    }

    // UPDATE PROFILE
    public CandidateProfile updateProfile(
            Long userId,
            CandidateProfileRequest request) {

        CandidateProfile profile =
                getProfileByUser(userId);

        profile.setHeadline(request.getHeadline());
        profile.setSummary(request.getSummary());
        profile.setSkills(request.getSkills());
        profile.setEducation(request.getEducation());
        profile.setExperienceYears(
                request.getExperienceYears()
        );

        return profileRepository.save(profile);
    }
}