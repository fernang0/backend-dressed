package cl.dressed.backend.module.profile.controller;

import cl.dressed.backend.module.profile.dto.UserSizeDto;
import cl.dressed.backend.module.profile.dto.UserSizeDto.UserSizeResponse;
import cl.dressed.backend.module.profile.dto.UserSizeDto.UserSizeUpdateRequest;
import cl.dressed.backend.module.profile.service.UserSizeService;
import cl.dressed.backend.module.auth.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/sizes")
@RequiredArgsConstructor
public class UserSizeController {

    private final UserSizeService userSizeService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<UserSizeResponse> getSizes(HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(userSizeService.getSizes(userId));
    }

    @PutMapping
    public ResponseEntity<UserSizeResponse> updateSizes(
            @Valid @RequestBody UserSizeUpdateRequest dto,
            HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromRequest(request);
        return ResponseEntity.ok(userSizeService.updateSizes(userId, dto));
    }

    @PutMapping("/proportions")
    public ResponseEntity<?> updateProportions(
    @Valid @RequestBody UserSizeDto.BodyMeasurementsRequest dto,
    HttpServletRequest request) {

    Long userId = jwtService.getUserIdFromRequest(request);//temporal luego dejar el con token
    userSizeService.updateProportions(userId, dto);

    return ResponseEntity.ok("Proporciones guardadas correctamente");
}
}