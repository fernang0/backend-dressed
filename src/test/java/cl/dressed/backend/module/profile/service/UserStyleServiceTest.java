package cl.dressed.backend.module.profile.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.dressed.backend.module.profile.dto.ProfileStyleRequest;
import cl.dressed.backend.module.profile.dto.ProfileStyleResponse;
import cl.dressed.backend.module.profile.entity.UserStyle;
import cl.dressed.backend.module.profile.repository.UserStyleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserStyleServiceTest {

    @Mock
    private UserStyleRepository userStyleRepository;

    @InjectMocks
    private UserStyleService userStyleService;

    // ── Helpers ───────────────────────────────────────────────────────────────

    private UserStyle buildStyle(Integer userId, String style) {
        UserStyle us = new UserStyle();
        us.setUserId(userId);
        us.setStyle(style);
        return us;
    }

    // ── getStyles ─────────────────────────────────────────────────────────────

    @Test
    void getStylesShouldReturnStylesWhenUserHasData() {
        when(userStyleRepository.findByUserId(1))
            .thenReturn(List.of(buildStyle(1, "casual"), buildStyle(1, "formal")));

        ProfileStyleResponse response = userStyleService.getStyles(1);

        assertThat(response.styles()).containsExactlyInAnyOrder("casual", "formal");
    }

    @Test
    void getStylesShouldReturnEmptySetWhenUserHasNoStyles() {
        when(userStyleRepository.findByUserId(1)).thenReturn(List.of());

        ProfileStyleResponse response = userStyleService.getStyles(1);

        assertThat(response.styles()).isEmpty();
    }

    // ── updateStyles ──────────────────────────────────────────────────────────

    @Test
    void updateStylesShouldReplaceExistingStylesWithNewOnes() {
        when(userStyleRepository.findByUserId(1))
            .thenReturn(List.of(buildStyle(1, "deportivo")));

        ProfileStyleRequest request = new ProfileStyleRequest(Set.of("deportivo"));
        userStyleService.updateStyles(1, request);

        verify(userStyleRepository).deleteByUserId(1);
        verify(userStyleRepository).saveAll(anyList());
    }

    @Test
    void updateStylesShouldThrowExceptionWhenStyleIsInvalid() {
        ProfileStyleRequest request = new ProfileStyleRequest(Set.of("punk_vikingo"));

        assertThatThrownBy(() -> userStyleService.updateStyles(1, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Estilos inválidos");
    }

    @Test
    void updateStylesShouldDeleteAllWhenEmptySetProvided() {
        when(userStyleRepository.findByUserId(1)).thenReturn(List.of());

        ProfileStyleRequest request = new ProfileStyleRequest(Set.of());
        ProfileStyleResponse response = userStyleService.updateStyles(1, request);

        verify(userStyleRepository).deleteByUserId(1);
        assertThat(response.styles()).isEmpty();
    }

    // ── getEffectiveStyles ────────────────────────────────────────────────────

    @Test
    void getEffectiveStylesShouldReturnUserStylesWhenTheyExist() {
        when(userStyleRepository.findByUserId(1))
            .thenReturn(List.of(buildStyle(1, "casual")));

        Set<String> result = userStyleService.getEffectiveStyles(1);

        assertThat(result).containsExactly("casual");
    }

    @Test
    void getEffectiveStylesShouldReturnAllValidStylesWhenUserHasNone() {
        when(userStyleRepository.findByUserId(1)).thenReturn(List.of());

        Set<String> result = userStyleService.getEffectiveStyles(1);

        // Si no tiene estilos, devuelve todos los válidos para no filtrar nada
        assertThat(result).containsExactlyInAnyOrder(
            "casual", "formal", "deportivo", "elegante", "streetwear"
        );
    }
}