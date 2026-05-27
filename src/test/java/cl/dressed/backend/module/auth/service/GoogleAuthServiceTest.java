package cl.dressed.backend.module.auth.service;

import cl.dressed.backend.module.auth.dto.GoogleAuthDto;
import cl.dressed.backend.module.auth.entity.User;
import cl.dressed.backend.module.auth.repository.UserRepository;
import cl.dressed.backend.module.auth.security.JwtService;
import cl.dressed.backend.module.auth.service.GoogleTokenVerifier.GoogleTokenVerificationException;
import cl.dressed.backend.module.auth.service.GoogleTokenVerifier.GoogleUserInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoogleAuthServiceTest {

    @Mock
    private GoogleTokenVerifier googleTokenVerifier;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private GoogleAuthService googleAuthService;

    private static final String CREDENTIAL = "fake-google-id-token";
    private static final String GOOGLE_ID  = "1234567890";
    private static final String EMAIL      = "user@gmail.com";
    private static final String JWT_TOKEN  = "dressed.jwt.token";

    @BeforeEach
    void setUp() {
        when(jwtService.generateToken(any(), anyString(), anyString())).thenReturn(JWT_TOKEN);
    }

    // ------------------------------------------------------------------
    // Caso 1: Usuario nuevo → se crea cuenta y isNewUser = true
    // ------------------------------------------------------------------
    @Test
    void loginWithGoogle_newUser_createsAccountAndReturnsToken() {
        when(googleTokenVerifier.verify(CREDENTIAL))
                .thenReturn(new GoogleUserInfo(GOOGLE_ID, EMAIL, "Test User"));
        when(userRepository.findByEmail(EMAIL.toLowerCase())).thenReturn(Optional.empty());

        User savedUser = buildUser(1L, EMAIL);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        GoogleAuthDto.GoogleLoginResponse response = googleAuthService.loginWithGoogle(
                new GoogleAuthDto.GoogleLoginRequest(CREDENTIAL));

        assertThat(response.isNewUser()).isTrue();
        assertThat(response.email()).isEqualTo(EMAIL);
        assertThat(response.token()).isEqualTo(JWT_TOKEN);

        verify(userRepository, times(1)).save(any(User.class));
    }

    // ------------------------------------------------------------------
    // Caso 2: Usuario ya existe sin proveedor OAuth → se vincula Google
    // ------------------------------------------------------------------
    @Test
    void loginWithGoogle_existingUserWithoutOauth_linksProviderAndReturnsToken() {
        when(googleTokenVerifier.verify(CREDENTIAL))
                .thenReturn(new GoogleUserInfo(GOOGLE_ID, EMAIL, "Test User"));

        User existingUser = buildUser(2L, EMAIL); // sin oauthProvider
        when(userRepository.findByEmail(EMAIL.toLowerCase()))
                .thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(existingUser);

        GoogleAuthDto.GoogleLoginResponse response = googleAuthService.loginWithGoogle(
                new GoogleAuthDto.GoogleLoginRequest(CREDENTIAL));

        assertThat(response.isNewUser()).isFalse();
        assertThat(response.token()).isEqualTo(JWT_TOKEN);
        assertThat(existingUser.getOauthProvider()).isEqualTo("google");
        assertThat(existingUser.getOauthProviderId()).isEqualTo(GOOGLE_ID);

        verify(userRepository, times(1)).save(existingUser);
    }

    // ------------------------------------------------------------------
    // Caso 3: Usuario ya existe y ya tiene Google vinculado → no guarda de nuevo
    // ------------------------------------------------------------------
    @Test
    void loginWithGoogle_existingUserAlreadyLinked_doesNotSaveAgain() {
        when(googleTokenVerifier.verify(CREDENTIAL))
                .thenReturn(new GoogleUserInfo(GOOGLE_ID, EMAIL, "Test User"));

        User existingUser = buildUser(3L, EMAIL);
        existingUser.setOauthProvider("google");
        existingUser.setOauthProviderId(GOOGLE_ID);
        when(userRepository.findByEmail(EMAIL.toLowerCase()))
                .thenReturn(Optional.of(existingUser));

        GoogleAuthDto.GoogleLoginResponse response = googleAuthService.loginWithGoogle(
                new GoogleAuthDto.GoogleLoginRequest(CREDENTIAL));

        assertThat(response.isNewUser()).isFalse();
        // No debe llamar a save() porque ya estaba vinculado
        verify(userRepository, never()).save(any());
    }

    // ------------------------------------------------------------------
    // Caso 4: Token de Google inválido → lanza excepción
    // ------------------------------------------------------------------
    @Test
    void loginWithGoogle_invalidToken_throwsException() {
        when(googleTokenVerifier.verify(CREDENTIAL))
                .thenThrow(new GoogleTokenVerificationException("Token expirado"));

        assertThatThrownBy(() -> googleAuthService.loginWithGoogle(
                new GoogleAuthDto.GoogleLoginRequest(CREDENTIAL)))
                .isInstanceOf(GoogleTokenVerificationException.class)
                .hasMessageContaining("Token expirado");

        verify(userRepository, never()).findByEmail(anyString());
        verify(userRepository, never()).save(any());
    }

    // ------------------------------------------------------------------
    // Helper
    // ------------------------------------------------------------------
    private User buildUser(Long id, String email) {
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash("");
        user.setActive(true);
        user.setRoles(List.of()); // sin roles → role por defecto "user"
        // Simular el id generado por la BD
        try {
            var field = User.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(user, id);
        } catch (Exception ignored) {
        }
        return user;
    }
}