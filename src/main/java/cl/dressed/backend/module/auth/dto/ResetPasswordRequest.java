package cl.dressed.backend.module.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

    @NotBlank(message = "token is required")
    @Size(max = 255, message = "token max length is 255")
    String token,

    @NotBlank(message = "password is required")
    @Size(min = 8, max = 72, message = "password length must be between 8 and 72")
    String newPassword

) {
}