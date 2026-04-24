package cl.dressed.backend.module.profile.service;

import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileResponse;
import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileUpdateRequest;
import cl.dressed.backend.module.profile.entity.Profile;
import cl.dressed.backend.module.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileResponse getProfile(Long userId) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyProfile(userId));

        return ProfileResponse.from(profile);
    }

    public ProfileResponse updateProfile(Long userId, ProfileUpdateRequest request) {

        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyProfile(userId));

        if (request.birthDate() != null) {
            profile.setBirthDate(request.birthDate());
        }

        if (request.gender() != null) {
            profile.setGender(request.gender());
        }

        profileRepository.save(profile);

        return ProfileResponse.from(profile);
    }

    private Profile createEmptyProfile(Long userId) {

        Profile profile = new Profile();
        profile.setUserId(userId);

        return profileRepository.save(profile);
    }
}