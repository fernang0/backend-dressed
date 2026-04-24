package cl.dressed.backend.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public final class AuthDto {

    private AuthDto() {
    }

    public record RegisterRequest(
        @NotBlank(message = "email is required")
        @Email(message = "email format is invalid")
        @Size(max = 255, message = "email max length is 255")
        String email,

        @NotBlank(message = "password is required")
        @Size(min = 8, max = 72, message = "password length must be between 8 and 72")
        String password
    ) {
    }

    public record RegisterResponse(
        Long id,
        String email,
        Boolean active,
        LocalDateTime createdAt,
        String token
    ) {
    }

    public record LoginRequest(
        @NotBlank(message = "email is required")
        @Email(message = "email format is invalid")
        String email,

        @NotBlank(message = "password is required")
        String password
    ) {
    }

    public record LoginResponse(
        Long id,
        String email,
        Boolean active,
        String token
    ) {
    }
}
