package cl.dressed.backend.module.profile.controller;


import cl.dressed.backend.module.profile.dto.UserMeasurementDto.UserMeasurementRequest;
import cl.dressed.backend.module.profile.dto.UserMeasurementDto.UserMeasurementResponse;
import cl.dressed.backend.module.profile.service.UserMeasurementService;
import cl.dressed.backend.module.auth.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/proportions")
@RequiredArgsConstructor
public class UserMeasurementController {

    private final UserMeasurementService userMeasurementService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<UserMeasurementResponse> getMeasurements(HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(userMeasurementService.getMeasurements(userId));
    }

    @PutMapping
    public ResponseEntity<UserMeasurementResponse> updateMeasurements(
            @Valid @RequestBody UserMeasurementRequest dto,
            HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(userMeasurementService.updateMeasurements(userId, dto));
    }
}