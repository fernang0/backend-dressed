package cl.dressed.backend.module.auth.dto;

import jakarta.validation.constraints.NotBlank;

public final class GoogleAuthDto {

    private GoogleAuthDto() {
    }

    public record GoogleLoginRequest(
        @NotBlank(message = "credential is required")
        String credential
    ) {
    }

    public record GoogleLoginResponse(
        Long id,
        String email,
        Boolean active,
        String token,
        boolean isNewUser
    ) {
    }
}