package cl.dressed.backend.module.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.dressed.backend.module.profile.dto.UserMeasurementDto.UserMeasurementRequest;
import cl.dressed.backend.module.profile.dto.UserMeasurementDto.UserMeasurementResponse;
import cl.dressed.backend.module.profile.entity.UserMeasurement;
import cl.dressed.backend.module.profile.repository.UserMeasurementRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserMeasurementServiceTest {

    @Mock
    private UserMeasurementRepository userMeasurementRepository;

    @InjectMocks
    private UserMeasurementService userMeasurementService;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private UserMeasurement buildMeasurement(Long userId) {
        UserMeasurement m = new UserMeasurement();
        m.setUserId(userId);
        m.setHeightCm(new BigDecimal("175.0"));
        m.setChestCm(new BigDecimal("90.0"));
        m.setWaistCm(new BigDecimal("75.0"));
        return m;
    }

    // ── getMeasurements ───────────────────────────────────────────────────────

    @Test
    void getMeasurementsShouldReturnDataWhenUserExists() {
        UserMeasurement m = buildMeasurement(1L);
        when(userMeasurementRepository.findByUserId(1L)).thenReturn(Optional.of(m));

        UserMeasurementResponse response = userMeasurementService.getMeasurements(1L);

        assertThat(response.heightCm()).isEqualByComparingTo("175.0");
        assertThat(response.chestCm()).isEqualByComparingTo("90.0");
        assertThat(response.waistCm()).isEqualByComparingTo("75.0");
    }

    @Test
    void getMeasurementsShouldThrowExceptionWhenUserNotFound() {
        when(userMeasurementRepository.findByUserId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userMeasurementService.getMeasurements(99L))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Medidas no encontradas para el usuario 99");
    }

    // ── updateMeasurements ────────────────────────────────────────────────────

    @Test
    void updateMeasurementsShouldCreateNewRecordWhenUserHasNone() {
        when(userMeasurementRepository.findByUserId(1L)).thenReturn(Optional.empty());
        UserMeasurement saved = buildMeasurement(1L);
        when(userMeasurementRepository.save(any(UserMeasurement.class))).thenReturn(saved);

        UserMeasurementRequest request = new UserMeasurementRequest(
            new BigDecimal("175.0"), null, null, null, null, null, null
        );
        userMeasurementService.updateMeasurements(1L, request);

        verify(userMeasurementRepository).save(any(UserMeasurement.class));
    }

    @Test
    void updateMeasurementsShouldUpdateOnlyProvidedFields() {
        UserMeasurement existing = buildMeasurement(1L);
        when(userMeasurementRepository.findByUserId(1L)).thenReturn(Optional.of(existing));
        when(userMeasurementRepository.save(any(UserMeasurement.class))).thenReturn(existing);

        UserMeasurementRequest request = new UserMeasurementRequest(
            new BigDecimal("180.0"), null, null, null, null, null, null
        );
        UserMeasurementResponse response = userMeasurementService.updateMeasurements(1L, request);

        assertThat(response.heightCm()).isEqualByComparingTo("180.0");
        // chest y waist no cambian
        assertThat(response.chestCm()).isEqualByComparingTo("90.0");
        assertThat(response.waistCm()).isEqualByComparingTo("75.0");
    }

    @Test
    void updateMeasurementsShouldUpdateAllFieldsWhenAllProvided() {
        UserMeasurement existing = buildMeasurement(1L);
        when(userMeasurementRepository.findByUserId(1L)).thenReturn(Optional.of(existing));
        when(userMeasurementRepository.save(any(UserMeasurement.class))).thenReturn(existing);

        UserMeasurementRequest request = new UserMeasurementRequest(
            new BigDecimal("180.0"),
            new BigDecimal("45.0"),
            new BigDecimal("95.0"),
            new BigDecimal("80.0"),
            new BigDecimal("100.0"),
            new BigDecimal("60.0"),
            new BigDecimal("85.0")
        );
        UserMeasurementResponse response = userMeasurementService.updateMeasurements(1L, request);

        assertThat(response.heightCm()).isEqualByComparingTo("180.0");
        assertThat(response.shouldersCm()).isEqualByComparingTo("45.0");
        assertThat(response.chestCm()).isEqualByComparingTo("95.0");
        assertThat(response.waistCm()).isEqualByComparingTo("80.0");
        assertThat(response.hipsCm()).isEqualByComparingTo("100.0");
        assertThat(response.torsoLengthCm()).isEqualByComparingTo("60.0");
        assertThat(response.legLengthCm()).isEqualByComparingTo("85.0");
    }
}