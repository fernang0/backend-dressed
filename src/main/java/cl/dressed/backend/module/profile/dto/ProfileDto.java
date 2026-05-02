package cl.dressed.backend.module.profile.dto;

import cl.dressed.backend.module.profile.entity.Profile;

import java.time.LocalDate;
import java.time.Period;

public class ProfileDto {

    public record ProfileUpdateRequest(
        String name,
        LocalDate birthDate,
        String gender
    ) {}

    // NUEVO
    public record SkinUpdateRequest(
        String skinTone,
        String colorPalette
    ) {}

    public record ProfileResponse(
        Long id,
        Long userId,
        String name,
        LocalDate birthDate,
        Integer age,
        String gender,
        String skinTone,      // NUEVO
        String colorPalette   // NUEVO
    ) {
        public static ProfileResponse from(Profile profile) {
            Integer age = null;
            if (profile.getBirthDate() != null &&
                !profile.getBirthDate().isAfter(LocalDate.now())) {
                age = Period.between(profile.getBirthDate(), LocalDate.now()).getYears();
            }
            return new ProfileResponse(
                profile.getId(),
                profile.getUserId(),
                profile.getName(),
                profile.getBirthDate(),
                age,
                profile.getGender(),
                profile.getSkinTone(),      // NUEVO
                profile.getColorPalette()   // NUEVO
            );
        }
    }
}