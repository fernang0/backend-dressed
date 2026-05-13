package cl.dressed.backend.module.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileResponse;
import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileUpdateRequest;
import cl.dressed.backend.module.profile.dto.ProfileDto.SkinUpdateRequest;
import cl.dressed.backend.module.profile.entity.Profile;
import cl.dressed.backend.module.profile.repository.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileService profileService;

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Profile buildProfile(Long userId) {
        Profile p = new Profile();
        p.setId(1L);
        p.setUserId(userId);
        return p;
    }

    // ── getProfile ────────────────────────────────────────────────────────────

    @Test
    void getProfileShouldReturnExistingProfile() {
        Profile profile = buildProfile(1L);
        profile.setName("Lucas");
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        ProfileResponse response = profileService.getProfile(1L);

        assertThat(response.userId()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Lucas");
    }

    @Test
    void getProfileShouldCreateEmptyProfileWhenNotFound() {
        Profile saved = buildProfile(1L);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenReturn(saved);

        ProfileResponse response = profileService.getProfile(1L);

        verify(profileRepository).save(any(Profile.class));
        assertThat(response.userId()).isEqualTo(1L);
    }

    // ── updateProfile ─────────────────────────────────────────────────────────

    @Test
    void updateProfileShouldUpdateNameWhenProvided() {
        Profile profile = buildProfile(1L);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        ProfileUpdateRequest request = new ProfileUpdateRequest("Lucas", null, null);
        ProfileResponse response = profileService.updateProfile(1L, request);

        assertThat(response.name()).isEqualTo("Lucas");
        verify(profileRepository).save(profile);
    }

    @Test
    void updateProfileShouldUpdateGenderWhenProvided() {
        Profile profile = buildProfile(1L);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        ProfileUpdateRequest request = new ProfileUpdateRequest(null, null, "male");
        ProfileResponse response = profileService.updateProfile(1L, request);

        assertThat(response.gender()).isEqualTo("male");
    }

    @Test
    void updateProfileShouldUpdateBirthDateWhenProvided() {
        Profile profile = buildProfile(1L);
        LocalDate birthDate = LocalDate.of(2000, 1, 1);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        ProfileUpdateRequest request = new ProfileUpdateRequest(null, birthDate, null);
        ProfileResponse response = profileService.updateProfile(1L, request);

        assertThat(response.birthDate()).isEqualTo(birthDate);
    }

    @Test
    void updateProfileShouldNotChangeFieldWhenNullIsPassed() {
        Profile profile = buildProfile(1L);
        profile.setName("Original");
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        ProfileUpdateRequest request = new ProfileUpdateRequest(null, null, null);
        ProfileResponse response = profileService.updateProfile(1L, request);

        assertThat(response.name()).isEqualTo("Original");
    }

    // ── updateSkin ────────────────────────────────────────────────────────────

    @Test
    void updateSkinShouldSetValidSkinTone() {
        Profile profile = buildProfile(1L);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        SkinUpdateRequest request = new SkinUpdateRequest("light", null);
        ProfileResponse response = profileService.updateSkin(1L, request);

        assertThat(response.skinTone()).isEqualTo("light");
    }

    @Test
    void updateSkinShouldThrowExceptionWhenSkinToneIsInvalid() {
        Profile profile = buildProfile(1L);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));

        SkinUpdateRequest request = new SkinUpdateRequest("verde_fluo", null);

        assertThatThrownBy(() -> profileService.updateSkin(1L, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("skin_tone inválido");
    }

    @Test
    void updateSkinShouldSetColorPaletteWhenProvided() {
        Profile profile = buildProfile(1L);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        SkinUpdateRequest request = new SkinUpdateRequest(null, "warm");
        ProfileResponse response = profileService.updateSkin(1L, request);

        assertThat(response.colorPalette()).isEqualTo("warm");
    }
}