package cl.dressed.backend.module.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import cl.dressed.backend.module.profile.dto.ProfileCompletenessResponse;
import cl.dressed.backend.module.profile.entity.Profile;
import cl.dressed.backend.module.profile.entity.UserSize;
import cl.dressed.backend.module.profile.entity.UserStyle;
import cl.dressed.backend.module.profile.entity.UserMeasurement;
import cl.dressed.backend.module.profile.repository.ProfileRepository;
import cl.dressed.backend.module.profile.repository.UserMeasurementRepository;
import cl.dressed.backend.module.profile.repository.UserSizeRepository;
import cl.dressed.backend.module.profile.repository.UserStyleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProfileCompletenessServiceTest {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserStyleRepository userStyleRepository;

    @Mock
    private UserMeasurementRepository userMeasurementRepository;

    @Mock
    private UserSizeRepository userSizeRepository;

    @InjectMocks
    private ProfileCompletenessService profileCompletenessService;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Profile buildFullProfile(Long userId) {
        Profile p = new Profile();
        p.setUserId(userId);
        p.setName("Lucas");
        p.setBirthDate(LocalDate.of(2000, 5, 1));
        p.setGender("male");
        p.setSkinTone("light");
        p.setColorPalette("warm");
        return p;
    }

    // ── Tests ─────────────────────────────────────────────────────────────────

    @Test
    void calculateShouldReturn100WhenAllFieldsAreComplete() {
        Profile profile = buildFullProfile(1L);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(userStyleRepository.findByUserId(1)).thenReturn(List.of(new UserStyle()));
        when(userSizeRepository.findByUserId(1L)).thenReturn(List.of(new UserSize()));
        when(userMeasurementRepository.findByUserId(1L)).thenReturn(Optional.of(new UserMeasurement()));

        ProfileCompletenessResponse response = profileCompletenessService.calculate(1L);

        assertThat(response.percentage()).isEqualTo(100);
        assertThat(response.missing()).isEmpty();
        assertThat(response.message()).contains("¡Perfil completo!");
    }

    @Test
    void calculateShouldReturn0WhenProfileDoesNotExist() {
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userStyleRepository.findByUserId(1)).thenReturn(List.of());
        when(userSizeRepository.findByUserId(1L)).thenReturn(List.of());
        when(userMeasurementRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ProfileCompletenessResponse response = profileCompletenessService.calculate(1L);

        assertThat(response.percentage()).isEqualTo(0);
        assertThat(response.missing()).containsExactlyInAnyOrder(
            "name", "birth_date", "gender", "skin_tone", "color_palette",
            "styles", "sizes", "measurements"
        );
    }

    @Test
    void calculateShouldReportMissingFieldsWhenProfileIsPartial() {
        Profile profile = new Profile();
        profile.setUserId(1L);
        profile.setName("Lucas");
        // sin birthDate, gender, skinTone, colorPalette
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(userStyleRepository.findByUserId(1)).thenReturn(List.of());
        when(userSizeRepository.findByUserId(1L)).thenReturn(List.of());
        when(userMeasurementRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ProfileCompletenessResponse response = profileCompletenessService.calculate(1L);

        assertThat(response.missing()).contains("birth_date", "gender", "skin_tone", "color_palette");
        assertThat(response.missing()).doesNotContain("name");
    }

    @Test
    void calculateShouldNotIncludeSizesInMissingWhenUserHasSizes() {
        Profile profile = new Profile();
        profile.setUserId(1L);
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.of(profile));
        when(userStyleRepository.findByUserId(1)).thenReturn(List.of());
        when(userSizeRepository.findByUserId(1L)).thenReturn(List.of(new UserSize()));
        when(userMeasurementRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ProfileCompletenessResponse response = profileCompletenessService.calculate(1L);

        assertThat(response.missing()).doesNotContain("sizes");
    }

    @Test
    void calculateShouldReturnIncompleteMessageWhenNotAt100Percent() {
        when(profileRepository.findByUserId(1L)).thenReturn(Optional.empty());
        when(userStyleRepository.findByUserId(1)).thenReturn(List.of());
        when(userSizeRepository.findByUserId(1L)).thenReturn(List.of());
        when(userMeasurementRepository.findByUserId(1L)).thenReturn(Optional.empty());

        ProfileCompletenessResponse response = profileCompletenessService.calculate(1L);

        assertThat(response.message()).contains("Completa tu perfil");
    }
}