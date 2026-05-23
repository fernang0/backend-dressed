package cl.dressed.backend.shared.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    public void sendEmail(String to, String subject, String body) {
        log.info("========== EMAIL ==========");
        log.info("TO: {}", to);
        log.info("SUBJECT: {}", subject);
        log.info("BODY: {}", body);
        log.info("===========================");
    }
}