package cl.dressed.backend.module.profile.controller;

import cl.dressed.backend.module.profile.dto.UserSizeDto.UserSizeResponse;
import cl.dressed.backend.module.profile.dto.UserSizeDto.UserSizeUpdateRequest;
import cl.dressed.backend.module.profile.service.UserSizeService;
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
@RequestMapping("/api/users/sizes")
@RequiredArgsConstructor
@Tag(name = "Tallas", description = "Gestión de tallas del usuario (top, bottom, calzado)")
@SecurityRequirement(name = "Bearer Token")
public class UserSizeController {

    private final UserSizeService userSizeService;
    private final JwtService jwtService;

    @Operation(summary = "Obtener tallas", description = "Retorna las tallas actuales del usuario autenticado")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tallas retornadas correctamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no enviado")
    })
    @GetMapping
    public ResponseEntity<UserSizeResponse> getSizes(HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(userSizeService.getSizes(userId));
    }

    @Operation(summary = "Actualizar tallas", description = "Actualiza una o más tallas del usuario. Los campos no enviados no se modifican")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Tallas actualizadas correctamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no enviado")
    })
    @PutMapping
    public ResponseEntity<UserSizeResponse> updateSizes(
            @Valid @RequestBody UserSizeUpdateRequest dto,
            HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(userSizeService.updateSizes(userId, dto));
    }
}