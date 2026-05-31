package cl.dressed.backend.module.profile.service;

import cl.dressed.backend.module.profile.dto.UserMeasurementDto.UserMeasurementRequest;
import cl.dressed.backend.module.profile.dto.UserMeasurementDto.UserMeasurementResponse;
import cl.dressed.backend.module.profile.entity.UserMeasurement;
import cl.dressed.backend.module.profile.repository.UserMeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserMeasurementService {

    private final UserMeasurementRepository userMeasurementRepository;

    public UserMeasurementResponse getMeasurements(Long userId) {
        return userMeasurementRepository.findByUserId(userId)
            .map(this::toResponse)
            .orElse(new UserMeasurementResponse(null, null, null, null, null, null, null));
    }

    public UserMeasurementResponse updateMeasurements(Long userId, UserMeasurementRequest request) {
        UserMeasurement m = userMeasurementRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserMeasurement newM = new UserMeasurement();
                    newM.setUserId(userId);
                    return newM;
                });

        if (request.heightCm() != null)      m.setHeightCm(request.heightCm());
        if (request.shouldersCm() != null)   m.setShouldersCm(request.shouldersCm());
        if (request.chestCm() != null)       m.setChestCm(request.chestCm());
        if (request.waistCm() != null)       m.setWaistCm(request.waistCm());
        if (request.hipsCm() != null)         m.setHipsCm(request.hipsCm());
        if (request.torsoLengthCm() != null) m.setTorsoLengthCm(request.torsoLengthCm());
        if (request.legLengthCm() != null)   m.setLegLengthCm(request.legLengthCm());

        m.setUpdatedAt(LocalDateTime.now());
        userMeasurementRepository.save(m);
        return toResponse(m);
    }

    private UserMeasurementResponse toResponse(UserMeasurement m) {
        return new UserMeasurementResponse(
            m.getHeightCm(),
            m.getShouldersCm(),
            m.getChestCm(),
            m.getWaistCm(),
            m.getHipsCm(),
            m.getTorsoLengthCm(),
            m.getLegLengthCm()
        );
    }
}
