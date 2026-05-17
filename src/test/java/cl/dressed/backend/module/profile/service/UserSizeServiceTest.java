package cl.dressed.backend.module.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.dressed.backend.module.profile.dto.UserSizeDto.UserSizeResponse;
import cl.dressed.backend.module.profile.dto.UserSizeDto.UserSizeUpdateRequest;
import cl.dressed.backend.module.profile.entity.UserSize;
import cl.dressed.backend.module.profile.repository.UserSizeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class UserSizeServiceTest {

    @Mock
    private UserSizeRepository userSizeRepository;

    @InjectMocks
    private UserSizeService userSizeService;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private UserSize buildSize(Long userId, String type, String value) {
        UserSize s = new UserSize();
        s.setUserId(userId);
        s.setType(type);
        s.setValue(value);
        return s;
    }

    // ── getSizes ──────────────────────────────────────────────────────────────

    @Test
    void getSizesShouldReturnAllSizesWhenUserHasData() {
        List<UserSize> sizes = List.of(
            buildSize(1L, "top", "M"),
            buildSize(1L, "bottom", "L"),
            buildSize(1L, "shoes", "42")
        );
        when(userSizeRepository.findByUserId(1L)).thenReturn(sizes);

        UserSizeResponse response = userSizeService.getSizes(1L);

        assertThat(response.top()).isEqualTo("M");
        assertThat(response.bottom()).isEqualTo("L");
        assertThat(response.shoes()).isEqualTo("42");
    }

    @Test
    void getSizesShouldReturnNullsWhenUserHasNoData() {
        when(userSizeRepository.findByUserId(1L)).thenReturn(List.of());

        UserSizeResponse response = userSizeService.getSizes(1L);

        assertThat(response.top()).isNull();
        assertThat(response.bottom()).isNull();
        assertThat(response.shoes()).isNull();
    }

    // ── updateSizes ───────────────────────────────────────────────────────────

    @Test
    void updateSizesShouldSaveOnlyProvidedFields() {
        when(userSizeRepository.findByUserIdAndType(1L, "top")).thenReturn(Optional.empty());
        when(userSizeRepository.findByUserId(1L)).thenReturn(List.of(buildSize(1L, "top", "S")));

        UserSizeUpdateRequest request = new UserSizeUpdateRequest("S", null, null);
        UserSizeResponse response = userSizeService.updateSizes(1L, request);

        verify(userSizeRepository, times(1)).save(any(UserSize.class));
        assertThat(response.top()).isEqualTo("S");
        assertThat(response.bottom()).isNull();
        assertThat(response.shoes()).isNull();
    }

    @Test
    void updateSizesShouldUpdateExistingEntryWhenAlreadyExists() {
        UserSize existing = buildSize(1L, "top", "M");
        when(userSizeRepository.findByUserIdAndType(1L, "top")).thenReturn(Optional.of(existing));
        when(userSizeRepository.findByUserId(1L)).thenReturn(List.of(buildSize(1L, "top", "XL")));

        UserSizeUpdateRequest request = new UserSizeUpdateRequest("XL", null, null);
        userSizeService.updateSizes(1L, request);

        verify(userSizeRepository).save(existing);
        assertThat(existing.getValue()).isEqualTo("XL");
    }

    @Test
    void updateSizesShouldSaveAllThreeFieldsWhenAllProvided() {
        when(userSizeRepository.findByUserIdAndType(1L, "top")).thenReturn(Optional.empty());
        when(userSizeRepository.findByUserIdAndType(1L, "bottom")).thenReturn(Optional.empty());
        when(userSizeRepository.findByUserIdAndType(1L, "shoes")).thenReturn(Optional.empty());
        when(userSizeRepository.findByUserId(1L)).thenReturn(List.of(
            buildSize(1L, "top", "M"),
            buildSize(1L, "bottom", "L"),
            buildSize(1L, "shoes", "42")
        ));

        UserSizeUpdateRequest request = new UserSizeUpdateRequest("M", "L", "42");
        UserSizeResponse response = userSizeService.updateSizes(1L, request);

        verify(userSizeRepository, times(3)).save(any(UserSize.class));
        assertThat(response.top()).isEqualTo("M");
        assertThat(response.bottom()).isEqualTo("L");
        assertThat(response.shoes()).isEqualTo("42");
    }
}