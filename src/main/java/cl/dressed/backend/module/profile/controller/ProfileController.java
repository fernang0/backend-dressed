package cl.dressed.backend.module.profile.controller;

import cl.dressed.backend.module.profile.dto.ProfileCompletenessResponse;
import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileResponse;
import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileUpdateRequest;
import cl.dressed.backend.module.profile.dto.ProfileDto.SkinUpdateRequest;
import cl.dressed.backend.module.profile.dto.ProfileStyleRequest;
import cl.dressed.backend.module.profile.dto.ProfileStyleResponse;
import cl.dressed.backend.module.profile.service.ProfileCompletenessService;
import cl.dressed.backend.module.profile.service.ProfileService;
import cl.dressed.backend.module.profile.service.UserStyleService;
import cl.dressed.backend.module.auth.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/profile")
@RequiredArgsConstructor
@Tag(name = "Perfil", description = "Gestión del perfil del usuario")
@SecurityRequirement(name = "Bearer Token")
public class ProfileController {

    private final ProfileService profileService;
    private final JwtService jwtService;
    private final UserStyleService userStyleService;
    private final ProfileCompletenessService profileCompletenessService;

    @Operation(summary = "Obtener perfil", description = "Retorna el perfil del usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil retornado correctamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no enviado")
    })
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile(HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(profileService.getProfile(userId));
    }

    @Operation(summary = "Actualizar perfil", description = "Actualiza nombre, fecha de nacimiento y/o género del usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Perfil actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no enviado")
    })
    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(
            @Valid @RequestBody ProfileUpdateRequest dto,
            HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(profileService.updateProfile(userId, dto));
    }

    @Operation(summary = "Actualizar tono de piel", description = "Actualiza el skin tone y/o paleta de color del usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Skin tone actualizado correctamente"),
        @ApiResponse(responseCode = "400", description = "Skin tone inválido"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no enviado")
    })
    @PutMapping("/skin")
    public ResponseEntity<ProfileResponse> updateSkin(
            @RequestBody SkinUpdateRequest dto,
            HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(profileService.updateSkin(userId, dto));
    }

    @Operation(summary = "Obtener estilos", description = "Retorna los estilos de moda configurados por el usuario")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estilos retornados correctamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no enviado")
    })
    @GetMapping("/styles")
    public ResponseEntity<ProfileStyleResponse> getStyles(HttpServletRequest request) {
        Integer userId = jwtService.getUserIdFromRequest(request).intValue();
        return ResponseEntity.ok(userStyleService.getStyles(userId));
    }

    @Operation(summary = "Actualizar estilos", description = "Reemplaza los estilos del usuario. Valores válidos: casual, formal, deportivo, elegante, streetwear")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Estilos actualizados correctamente"),
        @ApiResponse(responseCode = "400", description = "Estilo inválido"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no enviado")
    })
    @PutMapping("/styles")
    public ResponseEntity<ProfileStyleResponse> updateStyles(
            @Valid @RequestBody ProfileStyleRequest dto,
            HttpServletRequest request) {
        Integer userId = jwtService.getUserIdFromRequest(request).intValue();
        return ResponseEntity.ok(userStyleService.updateStyles(userId, dto));
    }

    @Operation(summary = "Completitud del perfil", description = "Retorna el porcentaje de completitud del perfil y los campos faltantes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Completitud calculada correctamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no enviado")
    })
    @GetMapping("/completeness")
    public ResponseEntity<ProfileCompletenessResponse> getCompleteness(HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(profileCompletenessService.calculate(userId));
    }
}