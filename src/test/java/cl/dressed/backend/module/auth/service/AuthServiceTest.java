package cl.dressed.backend.module.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import cl.dressed.backend.module.auth.dto.AuthDto;
import cl.dressed.backend.module.auth.entity.User;
import cl.dressed.backend.module.auth.exception.AuthException;
import cl.dressed.backend.module.auth.repository.UserRepository;
import cl.dressed.backend.module.auth.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldCreateUserWhenEmailIsValidAndPasswordHasMinEightCharacters() {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest("test@example.com", "password123");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashed-password");
        when(jwtService.generateToken(10L, "test@example.com")).thenReturn("jwt-token");

        User persistedUser = new User();
        persistedUser.setId(10L);
        persistedUser.setEmail("test@example.com");
        persistedUser.setPasswordHash("hashed-password");
        persistedUser.setActive(true);

        when(userRepository.save(any(User.class))).thenReturn(persistedUser);

        AuthDto.RegisterResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User toPersist = userCaptor.getValue();

        assertThat(toPersist.getEmail()).isEqualTo("test@example.com");
        assertThat(toPersist.getPasswordHash()).isEqualTo("hashed-password");
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.active()).isTrue();
        assertThat(response.token()).isEqualTo("jwt-token");
    }

    @Test
    void registerShouldReturnExpectedErrorWhenEmailAlreadyExists() {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest("test@example.com", "password123");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
            .isInstanceOf(AuthException.class)
            .hasMessage("El email ya está registrado");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("test@example.com", "password123");

        User user = new User();
        user.setId(10L);
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed-password");
        user.setActive(true);

        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed-password")).thenReturn(true);
        when(jwtService.generateToken(10L, "test@example.com")).thenReturn("jwt-token");

        AuthDto.LoginResponse response = authService.login(request);

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.active()).isTrue();
        assertThat(response.token()).isEqualTo("jwt-token");
    }

    @Test
    void loginShouldThrowExceptionWhenEmailDoesNotExist() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("nonexistent@example.com", "password123");

        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(java.util.Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(AuthException.class)
            .hasMessage("Credenciales inválidas");

        verify(passwordEncoder, never()).matches(any(), any());
    }

    @Test
    void loginShouldThrowExceptionWhenPasswordIsIncorrect() {
        AuthDto.LoginRequest request = new AuthDto.LoginRequest("test@example.com", "wrongpassword");

        User user = new User();
        user.setId(10L);
        user.setEmail("test@example.com");
        user.setPasswordHash("hashed-password");
        user.setActive(true);

        when(userRepository.findByEmail("test@example.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("wrongpassword", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request))
            .isInstanceOf(AuthException.class)
            .hasMessage("Credenciales inválidas");

        verify(jwtService, never()).generateToken(any(), any());
    }
}
