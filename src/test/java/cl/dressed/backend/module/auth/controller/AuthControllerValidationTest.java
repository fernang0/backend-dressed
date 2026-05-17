package cl.dressed.backend.module.auth.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
class AuthControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private cl.dressed.backend.module.auth.security.JwtService jwtService;

    @Test
    void registerShouldReturnValidationErrorWhenPasswordIsShorterThanEightChars() throws Exception {
        String requestBody = """
            {
              \"email\": \"test@example.com\",
              \"password\": \"1234567\"
            }
            """;

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("validation failed"))
            .andExpect(jsonPath("$.fields.password").value("password length must be between 8 and 72"));

        verifyNoInteractions(authService);
    }

    @Test
    void loginShouldReturnValidationErrorWhenEmailIsEmpty() throws Exception {
        String requestBody = """
            {
              \"email\": \"\",
              \"password\": \"password123\"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("validation failed"))
            .andExpect(jsonPath("$.fields.email").value("email is required"));

        verifyNoInteractions(authService);
    }

    @Test
    void loginShouldReturnValidationErrorWhenPasswordIsEmpty() throws Exception {
        String requestBody = """
            {
              \"email\": \"test@example.com\",
              \"password\": \"\"
            }
            """;

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("validation failed"))
            .andExpect(jsonPath("$.fields.password").value("password is required"));

        verifyNoInteractions(authService);
    }
}
