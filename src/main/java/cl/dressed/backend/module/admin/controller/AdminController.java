package cl.dressed.backend.module.admin.controller;

import cl.dressed.backend.module.auth.security.JwtService;
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
public class AdminController {

    private final JwtService jwtService;

    @Value("${app.scraper.base-url}")
    private String scraperBaseUrl;

    @Value("${app.scraper.api-key}")
    private String scraperApiKey;

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