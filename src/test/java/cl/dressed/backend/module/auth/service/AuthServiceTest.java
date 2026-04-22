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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerShouldCreateUserWhenEmailIsValidAndPasswordHasMinEightCharacters() {
        AuthDto.RegisterRequest request = new AuthDto.RegisterRequest("test@example.com", "password123");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);

        User persistedUser = new User();
        persistedUser.setId(10L);
        persistedUser.setEmail("test@example.com");
        persistedUser.setPassword("password123");
        persistedUser.setRole("USER");

        when(userRepository.save(any(User.class))).thenReturn(persistedUser);

        AuthDto.RegisterResponse response = authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User toPersist = userCaptor.getValue();

        assertThat(toPersist.getEmail()).isEqualTo("test@example.com");
        assertThat(toPersist.getPassword()).isEqualTo("password123");
        assertThat(toPersist.getRole()).isEqualTo("USER");
        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.email()).isEqualTo("test@example.com");
        assertThat(response.role()).isEqualTo("USER");
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
}
