package cl.dressed.backend.module.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ForgotPasswordRequest(

    @NotBlank(message = "email is required")
    @Email(message = "email format is invalid")
    @Size(max = 255, message = "email max length is 255")
    String email

) {
}