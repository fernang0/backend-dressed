package cl.dressed.backend.module.profile.controller;

import cl.dressed.backend.module.profile.dto.UserMeasurementDto.UserMeasurementRequest;
import cl.dressed.backend.module.profile.dto.UserMeasurementDto.UserMeasurementResponse;
import cl.dressed.backend.module.profile.service.UserMeasurementService;
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
@RequestMapping("/api/users/proportions")
@RequiredArgsConstructor
@Tag(name = "Proporciones", description = "Gestión de medidas corporales del usuario")
@SecurityRequirement(name = "Bearer Token")
public class UserMeasurementController {

    private final UserMeasurementService userMeasurementService;
    private final JwtService jwtService;

    @Operation(summary = "Obtener medidas", description = "Retorna las medidas corporales actuales del usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Medidas retornadas correctamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no enviado")
    })
    @GetMapping
    public ResponseEntity<UserMeasurementResponse> getMeasurements(HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(userMeasurementService.getMeasurements(userId));
    }

    @Operation(summary = "Actualizar medidas", description = "Actualiza una o más medidas corporales del usuario. Los campos no enviados no se modifican")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Medidas actualizadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos (ej: altura fuera de rango)"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no enviado")
    })
    @PutMapping
    public ResponseEntity<UserMeasurementResponse> updateMeasurements(
            @Valid @RequestBody UserMeasurementRequest dto,
            HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(userMeasurementService.updateMeasurements(userId, dto));
    }
}