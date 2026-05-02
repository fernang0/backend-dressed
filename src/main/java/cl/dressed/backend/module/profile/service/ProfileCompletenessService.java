package cl.dressed.backend.module.profile.service;

import cl.dressed.backend.module.profile.dto.ProfileCompletenessResponse;
import cl.dressed.backend.module.profile.entity.Profile;
import cl.dressed.backend.module.profile.repository.ProfileRepository;
import cl.dressed.backend.module.profile.repository.UserMeasurementRepository;
import cl.dressed.backend.module.profile.repository.UserSizeRepository;
import cl.dressed.backend.module.profile.repository.UserStyleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileCompletenessService {

    private final ProfileRepository          profileRepository;
    private final UserStyleRepository        userStyleRepository;
    private final UserMeasurementRepository  userMeasurementRepository;
    private final UserSizeRepository         userSizeRepository;

    // Cada campo vale 1 punto — total 8
    private static final int TOTAL_FIELDS = 8;

    public ProfileCompletenessResponse calculate(Long userId) {
        List<String> missing = new ArrayList<>();
        int completed = 0;

        // ── PROFILES ─────────────────────────────────────────────────────────
        Profile profile = profileRepository.findByUserId(userId).orElse(null);

        if (profile != null && hasValue(profile.getName())) {
            completed++;
        } else {
            missing.add("name");
        }

        if (profile != null && profile.getBirthDate() != null) {
            completed++;
        } else {
            missing.add("birth_date");
        }

        if (profile != null && hasValue(profile.getGender())) {
            completed++;
        } else {
            missing.add("gender");
        }

        if (profile != null && hasValue(profile.getSkinTone())) {
            completed++;
        } else {
            missing.add("skin_tone");
        }

        if (profile != null && hasValue(profile.getColorPalette())) {
            completed++;
        } else {
            missing.add("color_palette");
        }

        // ── USER_STYLES ───────────────────────────────────────────────────────
        if (!userStyleRepository.findByUserId(userId.intValue()).isEmpty()) {
            completed++;
        } else {
            missing.add("styles");
        }

        // ── USER_SIZES ────────────────────────────────────────────────────────
        if (!userSizeRepository.findByUserId(userId).isEmpty()) {
            completed++;
        } else {
            missing.add("sizes");
        }

        // ── USER_MEASUREMENTS ─────────────────────────────────────────────────
        if (userMeasurementRepository.findByUserId(userId).isPresent()) {
            completed++;
        } else {
            missing.add("measurements");
        }

        // ── RESULTADO ─────────────────────────────────────────────────────────
        int percentage = (completed * 100) / TOTAL_FIELDS;
        String message = percentage == 100
                ? "¡Perfil completo! Ya podemos generar outfits personalizados para ti."
                : "Completa tu perfil para obtener mejores recomendaciones.";

        return new ProfileCompletenessResponse(percentage, missing, message);
    }

    private boolean hasValue(String value) {
        return value != null && !value.isBlank();
    }
}