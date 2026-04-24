package cl.dressed.backend.shared.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendEmail(String to, String subject, String body) {
        // Mock temporal (luego se reemplaza por SMTP o proveedor real)

        System.out.println("========== EMAIL ==========");
        System.out.println("TO: " + to);
        System.out.println("SUBJECT: " + subject);
        System.out.println("BODY: " + body);
        System.out.println("===========================");
    }
}