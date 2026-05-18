package cl.dressed.backend.module.admin.controller;

import cl.dressed.backend.module.auth.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AdminController - Pruebas unitarias")
class AdminControllerTest {

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adminController, "scraperBaseUrl", "http://scraper-mock:8000");
        ReflectionTestUtils.setField(adminController, "scraperApiKey", "test-api-key");
    }

    @Test
    @DisplayName("triggerScraping: debe retornar 403 cuando el rol no es admin")
    void triggerScraping_rolNoAdmin_retorna403() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtService.getUserIdFromRequest(request)).thenReturn(1L);
        when(jwtService.getRoleFromRequest(request)).thenReturn("user");

        ResponseEntity<String> respuesta = adminController.triggerScraping(request);

        assertThat(respuesta.getStatusCode().value()).isEqualTo(403);
        assertThat(respuesta.getBody()).isEqualTo("Acceso denegado");
    }

    @Test
    @DisplayName("triggerScraping: debe retornar 500 cuando el scraper no responde")
    void triggerScraping_scraperNoDisponible_retorna500() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(jwtService.getUserIdFromRequest(request)).thenReturn(1L);
        when(jwtService.getRoleFromRequest(request)).thenReturn("admin");

        // El scraper no existe en tests, lanzará excepción de conexión
        ResponseEntity<String> respuesta = adminController.triggerScraping(request);

        assertThat(respuesta.getStatusCode().value()).isEqualTo(500);
        assertThat(respuesta.getBody()).contains("Error al contactar el scraper");
    }
}