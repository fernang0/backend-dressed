package cl.dressed.backend.module.profile.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.dressed.backend.module.auth.security.JwtService;
import cl.dressed.backend.module.profile.dto.UserSizeDto.UserSizeResponse;
import cl.dressed.backend.module.profile.dto.UserSizeDto.UserSizeUpdateRequest;
import cl.dressed.backend.module.profile.service.UserSizeService;
import cl.dressed.backend.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(UserSizeController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class UserSizeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserSizeService userSizeService;

    @MockBean
    private JwtService jwtService;

    // ── GET /api/users/sizes ───────────────────────────────────────────────────

    @Test
    void getSizesShouldReturn200WithSizesData() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        UserSizeResponse response = new UserSizeResponse("M", "L", "42");
        when(userSizeService.getSizes(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/sizes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.top").value("M"))
            .andExpect(jsonPath("$.bottom").value("L"))
            .andExpect(jsonPath("$.shoes").value("42"));
    }

    @Test
    void getSizesShouldReturn200WithNullsWhenUserHasNoSizes() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        UserSizeResponse response = new UserSizeResponse(null, null, null);
        when(userSizeService.getSizes(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/sizes"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.top").doesNotExist())
            .andExpect(jsonPath("$.bottom").doesNotExist());
    }

    // ── PUT /api/users/sizes ───────────────────────────────────────────────────

    @Test
    void updateSizesShouldReturn200WhenRequestIsValid() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        UserSizeResponse response = new UserSizeResponse("S", "M", "41");
        when(userSizeService.updateSizes(eq(1L), any(UserSizeUpdateRequest.class))).thenReturn(response);

        String body = """
            {
              "top": "S",
              "bottom": "M",
              "shoes": "41"
            }
            """;

        mockMvc.perform(put("/api/users/sizes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.top").value("S"))
            .andExpect(jsonPath("$.bottom").value("M"))
            .andExpect(jsonPath("$.shoes").value("41"));
    }

    @Test
    void updateSizesShouldReturn200WhenOnlyOneFieldProvided() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        UserSizeResponse response = new UserSizeResponse("XL", null, null);
        when(userSizeService.updateSizes(eq(1L), any(UserSizeUpdateRequest.class))).thenReturn(response);

        String body = """
            {
              "top": "XL"
            }
            """;

        mockMvc.perform(put("/api/users/sizes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.top").value("XL"));
    }
}