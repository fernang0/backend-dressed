package cl.dressed.backend.module.admin.service;

import cl.dressed.backend.module.admin.dto.AdminDto;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    public AdminDto.AdminStatusResponse health() {
        return new AdminDto.AdminStatusResponse("UP", "Admin module available");
    }
}
