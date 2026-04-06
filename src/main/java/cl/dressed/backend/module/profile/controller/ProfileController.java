package cl.dressed.backend.module.profile.controller;

import cl.dressed.backend.module.profile.dto.ProfileDto;
import cl.dressed.backend.module.profile.service.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto.ProfileResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(profileService.findById(id));
    }
}
