package cl.dressed.backend.module.profile.service;

import cl.dressed.backend.module.profile.dto.ProfileStyleRequest;
import cl.dressed.backend.module.profile.dto.ProfileStyleResponse;
import cl.dressed.backend.module.profile.entity.UserStyle;
import cl.dressed.backend.module.profile.repository.UserStyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserStyleService {

    private final UserStyleRepository userStyleRepository;

    private static final Set<String> VALID_STYLES = Set.of(
            "casual", "formal", "deportivo", "elegante", "streetwear"
    );

    // ── GET ──────────────────────────────────────────────────────────────────

    public ProfileStyleResponse getStyles(Integer userId) {
        Set<String> styles = userStyleRepository.findByUserId(userId)
                .stream()
                .map(UserStyle::getStyle)
                .collect(Collectors.toSet());
        return new ProfileStyleResponse(styles);
    }

    // ── PUT (replace completo) ────────────────────────────────────────────────

    @Transactional
    public ProfileStyleResponse updateStyles(Integer userId, ProfileStyleRequest request) {
        Set<String> styles = request.styles();

        if (styles != null && !styles.isEmpty()) {
            Set<String> invalid = styles.stream()
                    .filter(s -> !VALID_STYLES.contains(s))
                    .collect(Collectors.toSet());
            if (!invalid.isEmpty()) {
                throw new IllegalArgumentException(
                        "Estilos inválidos: " + invalid +
                        ". Valores permitidos: " + VALID_STYLES);
            }
        }

        // Reemplazar: borrar anteriores e insertar los nuevos
        userStyleRepository.deleteByUserId(userId);

        if (styles != null && !styles.isEmpty()) {
            List<UserStyle> entities = styles.stream()
                    .map(s -> {
                        UserStyle us = new UserStyle();
                        us.setUserId(userId);
                        us.setStyle(s);
                        return us;
                    })
                    .toList();
            userStyleRepository.saveAll(entities);
        }

        return getStyles(userId);
    }

    // ── HELPER para el motor de outfits ───────────────────────────────────────

    /**
     * Si el usuario no tiene estilos configurados, devuelve todos
     * para que el motor no aplique ningún filtro.
     */
    public Set<String> getEffectiveStyles(Integer userId) {
        Set<String> styles = userStyleRepository.findByUserId(userId)
                .stream()
                .map(UserStyle::getStyle)
                .collect(Collectors.toSet());
        return styles.isEmpty() ? VALID_STYLES : styles;
    }
}