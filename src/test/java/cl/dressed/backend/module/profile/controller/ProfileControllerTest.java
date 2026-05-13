package cl.dressed.backend.module.profile.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import cl.dressed.backend.module.auth.security.JwtService;
import cl.dressed.backend.module.profile.dto.ProfileCompletenessResponse;
import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileResponse;
import cl.dressed.backend.module.profile.dto.ProfileDto.ProfileUpdateRequest;
import cl.dressed.backend.module.profile.dto.ProfileDto.SkinUpdateRequest;
import cl.dressed.backend.module.profile.dto.ProfileStyleRequest;
import cl.dressed.backend.module.profile.dto.ProfileStyleResponse;
import cl.dressed.backend.module.profile.service.ProfileCompletenessService;
import cl.dressed.backend.module.profile.service.ProfileService;
import cl.dressed.backend.module.profile.service.UserStyleService;
import cl.dressed.backend.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

@WebMvcTest(ProfileController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private UserStyleService userStyleService;

    @MockBean
    private ProfileCompletenessService profileCompletenessService;

    @MockBean
    private JwtService jwtService;

    // ── GET /api/users/profile ─────────────────────────────────────────────────

    @Test
    void getProfileShouldReturn200WithProfileData() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        ProfileResponse profile = new ProfileResponse(1L, 1L, "Lucas", null, null, null, null, null);
        when(profileService.getProfile(1L)).thenReturn(profile);

        mockMvc.perform(get("/api/users/profile"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Lucas"))
            .andExpect(jsonPath("$.userId").value(1));
    }

    // ── PUT /api/users/profile ─────────────────────────────────────────────────

    @Test
    void updateProfileShouldReturn200WhenRequestIsValid() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        ProfileResponse updated = new ProfileResponse(1L, 1L, "Lucas Updated", null, null, "male", null, null);
        when(profileService.updateProfile(eq(1L), any(ProfileUpdateRequest.class))).thenReturn(updated);

        String body = """
            {
              "name": "Lucas Updated",
              "gender": "male"
            }
            """;

        mockMvc.perform(put("/api/users/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Lucas Updated"))
            .andExpect(jsonPath("$.gender").value("male"));
    }

    // ── PUT /api/users/profile/skin ────────────────────────────────────────────

    @Test
    void updateSkinShouldReturn200WhenSkinToneIsValid() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        ProfileResponse updated = new ProfileResponse(1L, 1L, null, null, null, null, "light", "warm");
        when(profileService.updateSkin(eq(1L), any(SkinUpdateRequest.class))).thenReturn(updated);

        String body = """
            {
              "skinTone": "light",
              "colorPalette": "warm"
            }
            """;

        mockMvc.perform(put("/api/users/profile/skin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.skinTone").value("light"))
            .andExpect(jsonPath("$.colorPalette").value("warm"));
    }

    // ── GET /api/users/profile/styles ─────────────────────────────────────────

    @Test
    void getStylesShouldReturn200WithUserStyles() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        ProfileStyleResponse stylesResponse = new ProfileStyleResponse(Set.of("casual", "formal"));
        when(userStyleService.getStyles(1)).thenReturn(stylesResponse);

        mockMvc.perform(get("/api/users/profile/styles"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.styles").isArray());
    }

    // ── PUT /api/users/profile/styles ─────────────────────────────────────────

    @Test
    void updateStylesShouldReturn200WhenStylesAreValid() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        ProfileStyleResponse stylesResponse = new ProfileStyleResponse(Set.of("casual"));
        when(userStyleService.updateStyles(eq(1), any(ProfileStyleRequest.class))).thenReturn(stylesResponse);

        String body = """
            {
              "styles": ["casual"]
            }
            """;

        mockMvc.perform(put("/api/users/profile/styles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.styles").isArray());
    }

    // ── GET /api/users/profile/completeness ───────────────────────────────────

    @Test
    void getCompletenessShouldReturn200WithPercentage() throws Exception {
        when(jwtService.getUserIdFromRequest(any())).thenReturn(1L);
        ProfileCompletenessResponse completeness = new ProfileCompletenessResponse(
            75, List.of("measurements"), "Completa tu perfil para obtener mejores recomendaciones."
        );
        when(profileCompletenessService.calculate(1L)).thenReturn(completeness);

        mockMvc.perform(get("/api/users/profile/completeness"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.percentage").value(75))
            .andExpect(jsonPath("$.missing[0]").value("measurements"));
    }
}