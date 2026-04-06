package cl.dressed.backend.module.admin.controller;

import cl.dressed.backend.module.admin.dto.AdminDto;
import cl.dressed.backend.module.admin.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/health")
    public ResponseEntity<AdminDto.AdminStatusResponse> health() {
        return ResponseEntity.ok(adminService.health());
    }
}
