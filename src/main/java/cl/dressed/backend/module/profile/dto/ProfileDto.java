package cl.dressed.backend.module.profile.dto;

import cl.dressed.backend.module.profile.entity.Profile;

import java.time.LocalDate;
import java.time.Period;

public class ProfileDto {

    public record ProfileUpdateRequest(
        LocalDate birthDate,
        String gender
    ) {}

    public record ProfileResponse(
        Long id,
        Long userId,
        LocalDate birthDate,
        Integer age,
        String gender
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
                profile.getBirthDate(),
                age,
                profile.getGender()
            );
        }
    }
}