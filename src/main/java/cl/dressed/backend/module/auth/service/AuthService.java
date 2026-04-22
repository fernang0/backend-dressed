package cl.dressed.backend.module.auth.service;

import cl.dressed.backend.module.auth.dto.AuthDto;
import cl.dressed.backend.module.auth.entity.User;
import cl.dressed.backend.module.auth.exception.AuthException;
import cl.dressed.backend.module.auth.repository.UserRepository;
import cl.dressed.backend.module.auth.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthDto.RegisterResponse register(AuthDto.RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new AuthException("El email ya está registrado");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setOauthProvider(null);
        user.setOauthProviderId(null);
        user.setActive(true);

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail());

        return new AuthDto.RegisterResponse(
            savedUser.getId(),
            savedUser.getEmail(),
            savedUser.getActive(),
            savedUser.getCreatedAt(),
            token
        );
    }

    @Transactional(readOnly = true)
    public AuthDto.LoginResponse login(AuthDto.LoginRequest request) {
        String email = request.email().trim().toLowerCase();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new AuthException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new AuthException("Credenciales inválidas");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());

        return new AuthDto.LoginResponse(
            user.getId(),
            user.getEmail(),
            user.getActive(),
            token
        );
    }
}
