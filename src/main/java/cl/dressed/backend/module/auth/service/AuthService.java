package cl.dressed.backend.module.auth.service;

import cl.dressed.backend.module.auth.dto.AuthDto;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public AuthDto.AuthResponse login(AuthDto.AuthRequest request) {
        return new AuthDto.AuthResponse("dummy-token", "Login successful");
    }
}
