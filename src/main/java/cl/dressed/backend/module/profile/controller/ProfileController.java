package cl.dressed.backend.module.profile.controller;

import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileResponse;
import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileUpdateRequest;
import cl.dressed.backend.module.profile.dto.ProfileDto.SkinUpdateRequest;
import cl.dressed.backend.module.profile.dto.ProfileStyleRequest;
import cl.dressed.backend.module.profile.dto.ProfileStyleResponse;
import cl.dressed.backend.module.profile.service.ProfileService;
import cl.dressed.backend.module.profile.service.UserStyleService;
import cl.dressed.backend.module.auth.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest dto,
            HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(profileService.updateProfile(userId, dto));
    }
    @PutMapping("/skin")
    public ResponseEntity<ProfileResponse> updateSkin(
            @RequestBody SkinUpdateRequest dto,
            HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(profileService.updateSkin(userId, dto));
    }
    // Nuevas dependencias en el constructor
    private final UserStyleService userStyleService;

    // Nuevo endpoint
    @GetMapping("/styles")
    public ResponseEntity<ProfileStyleResponse> getStyles(HttpServletRequest request) {
        Integer userId = jwtService.getUserIdFromRequest(request).intValue();
        return ResponseEntity.ok(userStyleService.getStyles(userId));
    }

    @PutMapping("/styles")
    public ResponseEntity<ProfileStyleResponse> updateStyles(
            @Valid @RequestBody ProfileStyleRequest dto,
            HttpServletRequest request) {
        Integer userId = jwtService.getUserIdFromRequest(request).intValue();
        return ResponseEntity.ok(userStyleService.updateStyles(userId, dto));
    }

}