package AI.Job.Application.Platform.controller;

import AI.Job.Application.Platform.dto.CandidateProfileRequest;
import AI.Job.Application.Platform.entity.CandidateProfile;
import AI.Job.Application.Platform.service.CandidateProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profiles")
public class CandidateProfileController {

    private final CandidateProfileService profileService;

    public CandidateProfileController(
            CandidateProfileService profileService) {

        this.profileService = profileService;
    }

    // CREATE PROFILE
    @PostMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CandidateProfile createProfile(
            @PathVariable Long userId,
            @RequestBody CandidateProfileRequest request) {

        return profileService.createProfile(
                userId,
                request
        );
    }

    // GET PROFILE
    @GetMapping("/user/{userId}")
    public CandidateProfile getProfile(
            @PathVariable Long userId) {

        return profileService.getProfileByUser(userId);
    }

    // UPDATE PROFILE
    @PutMapping("/user/{userId}")
    public CandidateProfile updateProfile(
            @PathVariable Long userId,
            @RequestBody CandidateProfileRequest request) {

        return profileService.updateProfile(
                userId,
                request
        );
    }
}