package cl.dressed.backend.module.auth.controller;

import cl.dressed.backend.module.auth.dto.AuthDto;
import cl.dressed.backend.module.auth.dto.ForgotPasswordRequest;
import cl.dressed.backend.module.auth.dto.ResetPasswordRequest;
import cl.dressed.backend.module.auth.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthDto.RegisterResponse> register(
            @Valid @RequestBody AuthDto.RegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(
            @Valid @RequestBody AuthDto.LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    // =========================
    // FORGOT PASSWORD
    // =========================
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    // =========================
    // RESET PASSWORD
    // =========================
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}