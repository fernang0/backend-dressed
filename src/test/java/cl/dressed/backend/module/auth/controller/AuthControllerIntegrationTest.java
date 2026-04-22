package cl.dressed.backend.module.auth.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.dressed.backend.module.auth.dto.AuthDto;
import cl.dressed.backend.module.auth.entity.User;
import cl.dressed.backend.module.auth.exception.AuthException;
import cl.dressed.backend.module.auth.service.AuthService;
import cl.dressed.backend.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Test
    void loginShouldReturnTokenWhenCredentialsAreValid() throws Exception {
        String requestBody = """
            {
              \"email\": \"test@example.com\",
              \"password\": \"password123\"
            }
            """;

        AuthDto.LoginResponse loginResponse = new AuthDto.LoginResponse(
            10L,
            "test@example.com",
            true,
            "valid-jwt-token"
        );

        when(authService.login(any(AuthDto.LoginRequest.class))).thenReturn(loginResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(10L))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.active").value(true))
            .andExpect(jsonPath("$.token").value("valid-jwt-token"));
    }

    @Test
    void loginShouldReturnUnauthorizedWhenCredentialsAreInvalid() throws Exception {
        String requestBody = """
            {
              \"email\": \"test@example.com\",
              \"password\": \"wrongpassword\"
            }
            """;

        when(authService.login(any(AuthDto.LoginRequest.class)))
            .thenThrow(new AuthException("Credenciales inválidas"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }

    @Test
    void loginShouldReturnUnauthorizedWhenEmailDoesNotExist() throws Exception {
        String requestBody = """
            {
              \"email\": \"nonexistent@example.com\",
              \"password\": \"password123\"
            }
            """;

        when(authService.login(any(AuthDto.LoginRequest.class)))
            .thenThrow(new AuthException("Credenciales inválidas"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.message").value("Credenciales inválidas"));
    }
}
