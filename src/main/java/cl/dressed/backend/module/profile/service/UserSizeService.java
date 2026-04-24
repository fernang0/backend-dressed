package cl.dressed.backend.module.profile.service;

import cl.dressed.backend.module.profile.dto.UserSizeDto;
import cl.dressed.backend.module.profile.dto.UserSizeDto.UserSizeResponse;
import cl.dressed.backend.module.profile.dto.UserSizeDto.UserSizeUpdateRequest;
import cl.dressed.backend.module.profile.entity.UserSize;
import cl.dressed.backend.module.profile.repository.UserSizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserSizeService {

    private final UserSizeRepository userSizeRepository;

    public UserSizeResponse getSizes(Long userId) {
        List<UserSize> sizes = userSizeRepository.findByUserId(userId);
        return toResponse(sizes);
    }

    public UserSizeResponse updateSizes(Long userId, UserSizeUpdateRequest request) {
        if (request.top() != null) upsert(userId, "top", request.top());
        if (request.bottom() != null) upsert(userId, "bottom", request.bottom());
        if (request.shoes() != null) upsert(userId, "shoes", request.shoes());
        return getSizes(userId);
    }

    public void updateProportions(Long userId, UserSizeDto.BodyMeasurementsRequest dto) {

        if (dto.altura() != null) {
            upsert(userId, "altura", String.valueOf(dto.altura()));
        }

        if (dto.pecho() != null) {
            upsert(userId, "pecho", String.valueOf(dto.pecho()));
        }

        if (dto.cintura() != null) {
            upsert(userId, "cintura", String.valueOf(dto.cintura()));
        }

        if (dto.cadera() != null) {
            upsert(userId, "cadera", String.valueOf(dto.cadera()));
        }
    }

    private void upsert(Long userId, String type, String value) {
        UserSize size = userSizeRepository
            .findByUserIdAndType(userId, type)
            .orElseGet(() -> {
                UserSize s = new UserSize();
                s.setUserId(userId);
                s.setType(type);
                return s;
            });
        size.setValue(value);
        size.setUpdatedAt(LocalDateTime.now());
        userSizeRepository.save(size);
    }

    private UserSizeResponse toResponse(List<UserSize> sizes) {
        String top = null, bottom = null, shoes = null;
        for (UserSize s : sizes) {
            switch (s.getType()) {
                case "top" -> top = s.getValue();
                case "bottom" -> bottom = s.getValue();
                case "shoes" -> shoes = s.getValue();
            }
        }
        return new UserSizeResponse(top, bottom, shoes);
    }
}