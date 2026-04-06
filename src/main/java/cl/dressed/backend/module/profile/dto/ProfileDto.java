package cl.dressed.backend.module.profile.dto;

import jakarta.validation.constraints.NotBlank;

public final class ProfileDto {

    private ProfileDto() {
    }

    public record ProfileRequest(
            @NotBlank String fullName,
            String phone
    ) {
    }

    public record ProfileResponse(
            Long id,
            String fullName,
            String phone
    ) {
    }
}
