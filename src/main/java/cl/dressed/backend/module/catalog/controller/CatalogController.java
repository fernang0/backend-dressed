package cl.dressed.backend.module.catalog.controller;

import cl.dressed.backend.module.catalog.dto.GarmentRequestDTO;
import cl.dressed.backend.module.catalog.dto.GarmentResponseDTO;
import cl.dressed.backend.module.catalog.security.ScraperApiKeyService;
import cl.dressed.backend.module.catalog.service.GarmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/catalog")
@RequiredArgsConstructor
@Tag(name = "Catálogo", description = "Gestión del catálogo de prendas")
public class CatalogController {

    private final GarmentService garmentService;
    private final ScraperApiKeyService scraperApiKeyService;

    @Operation(
        summary = "Importar prenda",
        description = "Importa una prenda al catálogo. Requiere header X-Api-Key válido. Usado por el scraper."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Prenda importada correctamente"),
        @ApiResponse(responseCode = "400", description = "Campos obligatorios faltantes o prenda duplicada"),
        @ApiResponse(responseCode = "401", description = "API key inválida o ausente")
    })
    @PostMapping("/products")
    public ResponseEntity<GarmentResponseDTO> importGarment(
            @RequestBody GarmentRequestDTO dto,
            HttpServletRequest request) {
        scraperApiKeyService.validateRequest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(garmentService.importGarment(dto));
    }

    @Operation(
        summary = "Obtener productos",
        description = "Retorna el catálogo de prendas con filtros opcionales y paginación"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Productos retornados correctamente")
    })
    @GetMapping("/products")
    public ResponseEntity<Page<GarmentResponseDTO>> getProducts(
            @Parameter(description = "Filtrar por categoría (ej: camisas, pantalones)") @RequestParam(required = false) String category,
            @Parameter(description = "Filtrar por talla (ej: M, L, XL)") @RequestParam(required = false) String size,
            @Parameter(description = "Filtrar por disponibilidad en stock") @RequestParam(required = false) Boolean inStock,
            Pageable pageable) {
        return ResponseEntity.ok(garmentService.getProducts(category, size, inStock, pageable));
    }
}