package cl.dressed.backend.module.profile.service;

import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileResponse;
import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileUpdateRequest;
import cl.dressed.backend.module.profile.dto.ProfileDto.SkinUpdateRequest;
import cl.dressed.backend.module.profile.entity.Profile;
import cl.dressed.backend.module.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Set;

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

        if (request.name() != null){
            profile.setName(request.name());
        }

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
    private static final Set<String> VALID_SKIN_TONES = Set.of(
    "very_light", "light", "medium_light", "medium",
    "medium_dark", "dark", "very_dark"
    );

    public ProfileResponse updateSkin(Long userId, SkinUpdateRequest request) {
        Profile profile = profileRepository.findByUserId(userId)
                .orElseGet(() -> createEmptyProfile(userId));

        if (request.skinTone() != null) {
            if (!VALID_SKIN_TONES.contains(request.skinTone())) {
                throw new IllegalArgumentException(
                    "skin_tone inválido. Valores permitidos: " + VALID_SKIN_TONES
                );
            }
            profile.setSkinTone(request.skinTone());
        }

        if (request.colorPalette() != null) {
            profile.setColorPalette(request.colorPalette());
        }

        profileRepository.save(profile);
        return ProfileResponse.from(profile);
    }
}