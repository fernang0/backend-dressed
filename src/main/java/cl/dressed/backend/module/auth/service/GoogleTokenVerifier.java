package cl.dressed.backend.module.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Verifica un ID token de Google llamando al endpoint público de Google:
 * https://oauth2.googleapis.com/tokeninfo?id_token=TOKEN
 *
 * No requiere ninguna librería cliente de Google — usa java.net.http.HttpClient (Java 11+).
 */
@Service
public class GoogleTokenVerifier {

    private static final String GOOGLE_TOKENINFO_URL =
            "https://oauth2.googleapis.com/tokeninfo?id_token=";

    @Value("${google.oauth.client-id}")
    private String expectedClientId;

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Verifica el token con Google y retorna los datos del usuario.
     *
     * @param idToken el credential que @react-oauth/google entrega al frontend
     * @return GoogleUserInfo con email, sub (Google user ID) y nombre
     * @throws GoogleTokenVerificationException si el token es inválido, expirado o no corresponde al client ID correcto
     */
    public GoogleUserInfo verify(String idToken) {
        JsonNode payload = callGoogleTokenInfo(idToken);

        // Verificar que el token fue emitido para nuestra app
        String aud = getField(payload, "aud");
        if (!expectedClientId.equals(aud)) {
            throw new GoogleTokenVerificationException(
                    "Token no corresponde a este cliente de Google");
        }

        // Google ya valida la expiración en el endpoint, pero hacemos check extra
        String emailVerified = getField(payload, "email_verified");
        if (!"true".equals(emailVerified)) {
            throw new GoogleTokenVerificationException(
                    "El email de Google no está verificado");
        }

        String email = getField(payload, "email");
        String sub   = getField(payload, "sub");
        String name  = payload.has("name") ? payload.get("name").asText() : null;

        return new GoogleUserInfo(sub, email, name);
    }

    private JsonNode callGoogleTokenInfo(String idToken) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GOOGLE_TOKENINFO_URL + idToken))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(
                    request, HttpResponse.BodyHandlers.ofString());

            JsonNode body = objectMapper.readTree(response.body());

            if (response.statusCode() != 200) {
                String errorDesc = body.has("error_description")
                        ? body.get("error_description").asText()
                        : "Token de Google inválido";
                throw new GoogleTokenVerificationException(errorDesc);
            }

            return body;

        } catch (GoogleTokenVerificationException e) {
            throw e;
        } catch (Exception e) {
            throw new GoogleTokenVerificationException(
                    "Error al verificar token con Google: " + e.getMessage());
        }
    }

    private String getField(JsonNode node, String field) {
        if (!node.has(field) || node.get(field).isNull()) {
            throw new GoogleTokenVerificationException(
                    "Campo requerido ausente en token de Google: " + field);
        }
        return node.get(field).asText();
    }

    // DTO interno de resultado
    public record GoogleUserInfo(String googleId, String email, String name) {
    }

    // Excepción interna para errores de verificación
    public static class GoogleTokenVerificationException extends RuntimeException {
        public GoogleTokenVerificationException(String message) {
            super(message);
        }
    }
}