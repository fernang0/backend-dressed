package cl.dressed.backend.module.auth.controller;

import cl.dressed.backend.module.auth.dto.AuthDto;
import cl.dressed.backend.module.auth.dto.ForgotPasswordRequest;
import cl.dressed.backend.module.auth.dto.ResetPasswordRequest;
import cl.dressed.backend.module.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Registro, login y recuperación de contraseña")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Registrar usuario", description = "Crea una nueva cuenta con email y contraseña")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o email ya registrado")
    })
    @PostMapping("/register")
    public ResponseEntity<AuthDto.RegisterResponse> register(
            @Valid @RequestBody AuthDto.RegisterRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authService.register(request));
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica al usuario y retorna un JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso, retorna token JWT"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    @PostMapping("/login")
    public ResponseEntity<AuthDto.LoginResponse> login(
            @Valid @RequestBody AuthDto.LoginRequest request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Solicitar recuperación de contraseña", description = "Envía un email con el enlace para resetear la contraseña")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Email enviado correctamente"),
        @ApiResponse(responseCode = "400", description = "Email inválido o no registrado")
    })
    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        authService.forgotPassword(request);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Resetear contraseña", description = "Cambia la contraseña usando el token recibido por email")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Contraseña actualizada correctamente"),
        @ApiResponse(responseCode = "400", description = "Token inválido o expirado")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        authService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}