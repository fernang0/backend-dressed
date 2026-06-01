package cl.dressed.backend.shared.exception;

import cl.dressed.backend.module.auth.exception.AuthException;
import cl.dressed.backend.module.auth.service.GoogleTokenVerifier.GoogleTokenVerificationException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException ex) {
        // Si el mensaje menciona credenciales, es un error de autenticación (401)
        // Si no, es un error de conflicto de registro (409)
        HttpStatus status = ex.getMessage().toLowerCase().contains("credencial")
            ? HttpStatus.UNAUTHORIZED
            : HttpStatus.CONFLICT;
        
        return ResponseEntity.status(status).body(errorBody(status, ex.getMessage()));
    }

    @ExceptionHandler(GoogleTokenVerificationException.class)
    public ResponseEntity<Map<String, Object>> handleGoogleTokenException(
            GoogleTokenVerificationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(errorBody(HttpStatus.UNAUTHORIZED, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> body = errorBody(HttpStatus.BAD_REQUEST, "validation failed");

        Map<String, String> fields = new LinkedHashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            fields.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        body.put("fields", fields);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    private Map<String, Object> errorBody(HttpStatus status, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }
}