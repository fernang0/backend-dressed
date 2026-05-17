package cl.dressed.backend.module.profile.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.dressed.backend.module.auth.security.JwtService;
import cl.dressed.backend.module.profile.dto.UserMeasurementDto.UserMeasurementRequest;
import cl.dressed.backend.module.profile.dto.UserMeasurementDto.UserMeasurementResponse;
import cl.dressed.backend.module.profile.service.UserMeasurementService;
import cl.dressed.backend.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

@WebMvcTest(UserMeasurementController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserMeasurementControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserMeasurementService userMeasurementService;

    @MockBean
    private JwtService jwtService;

    // ── GET /api/users/proportions ─────────────────────────────────────────────

    @Test
    void getMeasurementsShouldReturn200WithData() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        UserMeasurementResponse response = new UserMeasurementResponse(
            new BigDecimal("175.0"),
            new BigDecimal("45.0"),
            new BigDecimal("90.0"),
            new BigDecimal("75.0"),
            new BigDecimal("95.0"),
            new BigDecimal("55.0"),
            new BigDecimal("80.0")
        );
        when(userMeasurementService.getMeasurements(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/proportions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.heightCm").value(175.0))
            .andExpect(jsonPath("$.chestCm").value(90.0))
            .andExpect(jsonPath("$.waistCm").value(75.0));
    }

    @Test
    void getMeasurementsShouldReturn200WhenUserHasNoMeasurementsSetYet() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        UserMeasurementResponse empty = new UserMeasurementResponse(
        null, null, null, null, null, null, null
        );
        when(userMeasurementService.getMeasurements(1L)).thenReturn(empty);

        mockMvc.perform(get("/api/users/proportions"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.heightCm").doesNotExist());
    }

    // ── PUT /api/users/proportions ─────────────────────────────────────────────

    @Test
    void updateMeasurementsShouldReturn200WhenRequestIsValid() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        UserMeasurementResponse response = new UserMeasurementResponse(
            new BigDecimal("180.0"), null, null, null, null, null, null
        );
        when(userMeasurementService.updateMeasurements(eq(1L), any(UserMeasurementRequest.class)))
            .thenReturn(response);

        String body = """
            {
              "heightCm": 180.0
            }
            """;

        mockMvc.perform(put("/api/users/proportions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.heightCm").value(180.0));
    }

    @Test
    void updateMeasurementsShouldReturn400WhenHeightIsBelowMinimum() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);

        String body = """
            {
              "heightCm": 50.0
            }
            """;

        mockMvc.perform(put("/api/users/proportions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateMeasurementsShouldReturn400WhenHeightIsAboveMaximum() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);

        String body = """
            {
              "heightCm": 999.0
            }
            """;

        mockMvc.perform(put("/api/users/proportions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }
}