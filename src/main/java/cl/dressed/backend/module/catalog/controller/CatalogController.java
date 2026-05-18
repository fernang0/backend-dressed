package cl.dressed.backend.module.catalog.controller;

import cl.dressed.backend.module.catalog.dto.GarmentRequestDTO;
import cl.dressed.backend.module.catalog.dto.GarmentResponseDTO;
import cl.dressed.backend.module.catalog.security.ScraperApiKeyService;
import cl.dressed.backend.module.catalog.service.GarmentService;
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
public class CatalogController {

    private final GarmentService garmentService;
    private final ScraperApiKeyService scraperApiKeyService;

    @PostMapping("/products")
    public ResponseEntity<GarmentResponseDTO> importGarment(
            @RequestBody GarmentRequestDTO dto,
            HttpServletRequest request) {
        scraperApiKeyService.validateRequest(request);  // solo X-Api-Key, no JWT
        return ResponseEntity.status(HttpStatus.CREATED).body(garmentService.importGarment(dto));
    }

    @GetMapping("/products")
    public ResponseEntity<Page<GarmentResponseDTO>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String size,
            @RequestParam(required = false) Boolean inStock,
            Pageable pageable) {
        return ResponseEntity.ok(garmentService.getProducts(category, size, inStock, pageable));
    }
    
}