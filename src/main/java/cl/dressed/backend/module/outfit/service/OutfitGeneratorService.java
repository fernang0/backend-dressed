package cl.dressed.backend.module.outfit.service;

import cl.dressed.backend.module.catalog.repository.GarmentRepository;
import cl.dressed.backend.module.outfit.dto.OutfitGarmentItemDTO;
import cl.dressed.backend.module.outfit.dto.OutfitResponseDTO;
import cl.dressed.backend.module.outfit.entity.Outfit;
import cl.dressed.backend.module.outfit.entity.OutfitGarment;
import cl.dressed.backend.module.outfit.repository.OutfitGarmentRepository;
import cl.dressed.backend.module.outfit.repository.OutfitRepository;
import cl.dressed.backend.module.outfit.util.GenderConverter;
import cl.dressed.backend.module.profile.entity.Profile;
import cl.dressed.backend.module.profile.entity.UserSize;
import cl.dressed.backend.module.profile.repository.ProfileRepository;
import cl.dressed.backend.module.profile.repository.UserSizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OutfitGeneratorService {

    private final GarmentRepository garmentRepository;
    private final OutfitRepository outfitRepository;
    private final OutfitGarmentRepository outfitGarmentRepository;
    private final UserSizeRepository userSizeRepository;
    private final ProfileRepository profileRepository;

    /**
     * Retorna los outfits pre-generados por el modelo ML
     * cuyas prendas (top y bottom) sean compatibles con las tallas y género del usuario.
     */
    @Transactional(readOnly = true)
    public List<OutfitResponseDTO> getOutfitsForUser(Long userId) {

        // 1. Obtener tallas del usuario
        List<UserSize> userSizes = userSizeRepository.findByUserId(userId);
        if (userSizes.isEmpty()) {
            throw new IllegalStateException(
                "El usuario no tiene tallas registradas. Por favor completa tu perfil.");
        }

        Map<String, String> sizeByType = userSizes.stream()
            .collect(Collectors.toMap(
                us -> us.getType().toLowerCase(),
                us -> us.getValue().toUpperCase(),
                (a, b) -> a
            ));

        String topSize    = sizeByType.get("top");
        String bottomSize = sizeByType.get("bottom");

        if (topSize == null || bottomSize == null) {
            throw new IllegalStateException(
                "Se requieren tallas de top y bottom para generar outfits.");
        }

        // 2. Obtener gender del usuario desde su perfil
        Profile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new IllegalStateException(
                "El usuario no tiene perfil. Por favor completa tu perfil."));

        String userGenderEnglish = profile.getGender();
        if (userGenderEnglish == null) {
            throw new IllegalStateException(
                "El usuario no tiene gender registrado. Por favor completa tu perfil.");
        }

        // 3. Convertir gender a español para buscar prendas
        String userGenderSpanish = GenderConverter.convertProfileGenderToGarmentGender(userGenderEnglish);

        // 4. Buscar outfits compatibles (talla + gender)
        List<Outfit> outfits = outfitRepository.findCompatibleOutfits(
            topSize, bottomSize, userGenderSpanish);

        // 5. Armar respuesta
        return outfits.stream().map(outfit -> {
            List<OutfitGarment> outfitGarments =
                outfitGarmentRepository.findByOutfitId(outfit.getId());

            List<OutfitGarmentItemDTO> items = outfitGarments.stream()
                .map(og -> garmentRepository.findById(og.getGarmentId())
                    .map(g -> new OutfitGarmentItemDTO(
                        g.getId(), g.getName(), g.getCategory(), og.getRole(),
                        g.getSize(), g.getImageUrl(), g.getProductLink(),
                        g.getPrice(), g.getMainColor(), g.getFit(), g.getStyle()
                    ))
                    .orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            return new OutfitResponseDTO(
                outfit.getId(), outfit.getUserId(),
                outfit.getGeneratedAt(), outfit.getOrigin(), items
            );
        }).collect(Collectors.toList());
    }
}