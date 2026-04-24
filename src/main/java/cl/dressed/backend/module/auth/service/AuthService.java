package cl.dressed.backend.module.auth.service;

import cl.dressed.backend.module.auth.dto.AuthDto;
import cl.dressed.backend.module.auth.dto.ForgotPasswordRequest;
import cl.dressed.backend.module.auth.dto.ResetPasswordRequest;
import cl.dressed.backend.module.auth.entity.PasswordRecovery;
import cl.dressed.backend.module.auth.entity.User;
import cl.dressed.backend.module.auth.exception.AuthException;
import cl.dressed.backend.module.auth.repository.PasswordRecoveryRepository;
import cl.dressed.backend.module.auth.repository.UserRepository;
import cl.dressed.backend.module.auth.security.JwtService;
import cl.dressed.backend.shared.service.EmailService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PasswordRecoveryRepository passwordRecoveryRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Transactional
    public AuthDto.RegisterResponse register(AuthDto.RegisterRequest request) {

        String email = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new AuthException("El email ya está registrado");
        }

        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.password()));
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

    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {

        String email = request.email().trim().toLowerCase();

        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return;
        }

        passwordRecoveryRepository.invalidatePreviousTokens(user);

        String token = UUID.randomUUID().toString();

        PasswordRecovery recovery = new PasswordRecovery();
        recovery.setUser(user);
        recovery.setToken(token);
        recovery.setExpiresAt(LocalDateTime.now().plusMinutes(30));
        recovery.setUsed(false);

        passwordRecoveryRepository.save(recovery);

        String link = frontendUrl + "/reset-password?token=" + token;

        emailService.sendEmail(
                user.getEmail(),
                "Recuperación de contraseña",
                "Haz clic aquí para restablecer tu contraseña: " + link
        );
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {

        PasswordRecovery recovery = passwordRecoveryRepository.findByToken(request.token())
                .orElseThrow(() -> new AuthException("Token inválido"));

        if (Boolean.TRUE.equals(recovery.getUsed())) {
            throw new AuthException("El enlace ya fue utilizado");
        }

        if (recovery.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new AuthException("Enlace expirado");
        }

        User user = recovery.getUser();

        if (user == null) {
            throw new AuthException("Token inválido");
        }

        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        recovery.setUsed(true);
        passwordRecoveryRepository.save(recovery);
    }
}