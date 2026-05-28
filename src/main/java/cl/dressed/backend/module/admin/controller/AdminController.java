package cl.dressed.backend.module.admin.controller;

import cl.dressed.backend.module.admin.dto.AdminMetricsDTO;
import cl.dressed.backend.module.admin.dto.AdminUserDTO;
import cl.dressed.backend.module.admin.service.AdminService;
import cl.dressed.backend.module.auth.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final JwtService jwtService;

    @GetMapping("/metrics")
    public ResponseEntity<AdminMetricsDTO> getMetrics(HttpServletRequest request) {
        requireAdmin(request);
        return ResponseEntity.ok(adminService.getMetrics());
    }

    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserDTO>> getUsers(
        HttpServletRequest request,
        Pageable pageable
    ) {
        requireAdmin(request);
        return ResponseEntity.ok(adminService.getUsers(pageable));
    }

    private void requireAdmin(HttpServletRequest request) {
        try {
            String role = jwtService.getRoleFromRequest(request);
            if (!"admin".equals(role)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acceso denegado");
            }
        } catch (ResponseStatusException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
    }
}