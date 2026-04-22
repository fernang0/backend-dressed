package cl.dressed.backend.module.auth.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final byte[] signingSecret;
    private final long expirationMs;

    public JwtService(
        @Value("${app.jwt.secret}") String jwtSecret,
        @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        this.signingSecret = jwtSecret.getBytes(StandardCharsets.UTF_8);
        this.expirationMs = expirationMs;
    }

    public String generateToken(Long userId, String email) {
        Instant now = Instant.now();
        long issuedAt = now.getEpochSecond();
        long expiration = now.plusMillis(expirationMs).getEpochSecond();

        String headerJson = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payloadJson = "{\"sub\":\"" + escapeJson(email) + "\",\"uid\":" + userId
            + ",\"iat\":" + issuedAt + ",\"exp\":" + expiration + "}";

        String header = base64UrlEncode(headerJson.getBytes(StandardCharsets.UTF_8));
        String payload = base64UrlEncode(payloadJson.getBytes(StandardCharsets.UTF_8));
        String content = header + "." + payload;

        byte[] signature = hmacSha256(content.getBytes(StandardCharsets.UTF_8));
        return content + "." + base64UrlEncode(signature);
    }

    private byte[] hmacSha256(byte[] content) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(signingSecret, HMAC_ALGORITHM));
            return mac.doFinal(content);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el token JWT", ex);
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String escapeJson(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }

        String escaped = value.replace("\\", "\\\\").replace("\"", "\\\"");
        if (MessageDigest.isEqual(escaped.getBytes(StandardCharsets.UTF_8), value.getBytes(StandardCharsets.UTF_8))) {
            return value;
        }
        return escaped;
    }
}
