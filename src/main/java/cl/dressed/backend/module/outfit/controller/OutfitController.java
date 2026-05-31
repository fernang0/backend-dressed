package cl.dressed.backend.module.outfit.controller;

import cl.dressed.backend.module.auth.security.JwtService;
import cl.dressed.backend.module.outfit.dto.OutfitResponseDTO;
import cl.dressed.backend.module.outfit.service.OutfitGeneratorService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/outfits")
@RequiredArgsConstructor
public class OutfitController {

    private final OutfitGeneratorService outfitGeneratorService;
    private final JwtService jwtService;

    /**
     * POST /api/outfits/generate
     * Genera un outfit nuevo para el usuario autenticado basado en sus tallas.
     */
    @PostMapping("/generate")
    public ResponseEntity<OutfitResponseDTO> generateOutfit(HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(outfitGeneratorService.generateOutfit(userId));
    }

    /**
     * GET /api/outfits
     * Retorna todos los outfits generados para el usuario autenticado.
     */
    @GetMapping
    public ResponseEntity<List<OutfitResponseDTO>> getMyOutfits(HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(outfitGeneratorService.getOutfitsForUser(userId));
    }
}