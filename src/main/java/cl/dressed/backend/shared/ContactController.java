package cl.dressed.backend.shared;

import cl.dressed.backend.shared.dto.ContactRequest;
import cl.dressed.backend.shared.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final EmailService emailService;
    private static final String DEST = "dressed968@gmail.com";

    @PostMapping
    public ResponseEntity<Void> contact(@RequestBody ContactRequest req) {
        if (req.name() == null || req.email() == null || req.message() == null
                || req.name().isBlank() || req.email().isBlank() || req.message().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        String subject = "Nuevo mensaje de contacto de " + req.name();
        String html = """
            <div style="font-family:sans-serif;max-width:600px;margin:0 auto">
              <h2 style="color:#111">Nuevo mensaje de contacto</h2>
              <p><strong>Nombre:</strong> %s</p>
              <p><strong>Email:</strong> %s</p>
              <p><strong>Mensaje:</strong></p>
              <div style="background:#f5f5f5;padding:16px;border-radius:8px;white-space:pre-wrap">%s</div>
            </div>
            """.formatted(req.name(), req.email(), req.message());

        emailService.sendEmail(DEST, subject, html);
        return ResponseEntity.ok().build();
    }
}