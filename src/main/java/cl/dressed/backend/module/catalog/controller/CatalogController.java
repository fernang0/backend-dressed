package cl.dressed.backend.module.catalog.controller;

import cl.dressed.backend.module.catalog.dto.CatalogDto;
import cl.dressed.backend.module.catalog.service.CatalogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<CatalogDto.ProductResponse> findProduct(@PathVariable Long id) {
        return ResponseEntity.ok(catalogService.findProduct(id));
    }
}
