package cl.dressed.backend.module.catalog.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ScraperApiKeyService {

    @Value("${app.scraper.api-key}")
    private String apiKey;

    public void validateRequest(HttpServletRequest request) {
        String key = request.getHeader("X-Api-Key");
        if (key == null || !key.equals(apiKey)) {
            throw new IllegalArgumentException("API key inválida o ausente");
        }
    }
}