package cl.dressed.backend.module.profile.service;

import cl.dressed.backend.module.profile.dto.ProfileDto;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {

    public ProfileDto.ProfileResponse findById(Long id) {
        return new ProfileDto.ProfileResponse(id, "Demo User", "+56 9 0000 0000");
    }
}
