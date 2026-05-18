package cl.dressed.backend.module.admin.controller;

import cl.dressed.backend.module.auth.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Operaciones administrativas. Requiere rol admin")
@SecurityRequirement(name = "Bearer Token")
public class AdminController {

    private final JwtService jwtService;

    @Value("${app.scraper.base-url}")
    private String scraperBaseUrl;

    @Value("${app.scraper.api-key}")
    private String scraperApiKey;

    @Operation(
        summary = "Disparar scraping",
        description = "Lanza el proceso de scraping en el servidor externo. Solo accesible para usuarios con rol admin"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Scraping iniciado correctamente"),
        @ApiResponse(responseCode = "401", description = "Token inválido o no enviado"),
        @ApiResponse(responseCode = "403", description = "El usuario no tiene rol admin"),
        @ApiResponse(responseCode = "500", description = "Error al contactar el scraper")
    })
    @PostMapping("/scraping/run")
    public ResponseEntity<String> triggerScraping(HttpServletRequest request) {
        jwtService.getUserIdFromRequest(request);

        String role = jwtService.getRoleFromRequest(request);
        if (!"admin".equals(role)) {
            return ResponseEntity.status(403).body("Acceso denegado");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", scraperApiKey);

        try {
            new RestTemplate().postForEntity(
                scraperBaseUrl + "/run",
                new HttpEntity<>(headers),
                String.class
            );
            return ResponseEntity.ok("Scraping iniciado");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al contactar el scraper: " + e.getMessage());
        }
    }
}