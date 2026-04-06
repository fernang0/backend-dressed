package cl.dressed.backend.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public final class AuthDto {

    private AuthDto() {
    }

    public record AuthRequest(
            @Email String email,
            @NotBlank String password
    ) {
    }

    public record AuthResponse(
            String token,
            String message
    ) {
    }
}
