package cl.dressed.backend.module.auth.service;

import cl.dressed.backend.module.auth.dto.GoogleAuthDto;
import cl.dressed.backend.module.auth.entity.User;
import cl.dressed.backend.module.auth.repository.UserRepository;
import cl.dressed.backend.module.auth.security.JwtService;
import cl.dressed.backend.module.auth.service.GoogleTokenVerifier.GoogleUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GoogleAuthService {

    private static final String PROVIDER = "google";

    private final GoogleTokenVerifier googleTokenVerifier;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * Verifica el ID token de Google, crea o recupera el usuario en BD
     * y retorna un JWT de Dressed exactamente igual al que emite el login normal.
     */
    @Transactional
    public GoogleAuthDto.GoogleLoginResponse loginWithGoogle(
            GoogleAuthDto.GoogleLoginRequest request) {

        // 1. Verificar token con Google → obtener email + googleId
        GoogleUserInfo googleUser = googleTokenVerifier.verify(request.credential());

        String email = googleUser.email().trim().toLowerCase();
        String googleId = googleUser.googleId();

        // 2. Buscar usuario existente por email
        boolean isNewUser = false;
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // 3a. Nuevo usuario: crear cuenta OAuth (sin password)
            user = new User();
            user.setEmail(email);
            // Contraseña vacía marcada explícitamente como cuenta OAuth;
            // el login con email/password rechazará esto porque BCrypt nunca
            // matchea contra una cadena vacía.
            user.setPasswordHash("");
            user.setOauthProvider(PROVIDER);
            user.setOauthProviderId(googleId);
            user.setActive(true);
            user = userRepository.save(user);
            isNewUser = true;

        } else {
            // 3b. Usuario ya existe: vincular proveedor si todavía no está vinculado
            if (user.getOauthProvider() == null) {
                user.setOauthProvider(PROVIDER);
                user.setOauthProviderId(googleId);
                user = userRepository.save(user);
            }
        }

        // 4. Generar JWT de Dressed (mismo formato que login normal)
        String role = user.getRoles().isEmpty() ? "user" : user.getRoles().get(0).getName();
        String token = jwtService.generateToken(user.getId(), user.getEmail(), role);

        return new GoogleAuthDto.GoogleLoginResponse(
                user.getId(),
                user.getEmail(),
                user.getActive(),
                token,
                isNewUser
        );
    }
}