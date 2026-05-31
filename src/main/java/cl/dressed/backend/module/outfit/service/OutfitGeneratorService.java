package cl.dressed.backend.module.outfit.service;

import cl.dressed.backend.module.catalog.entity.Garment;
import cl.dressed.backend.module.catalog.repository.GarmentRepository;
import cl.dressed.backend.module.outfit.dto.OutfitGarmentItemDTO;
import cl.dressed.backend.module.outfit.dto.OutfitResponseDTO;
import cl.dressed.backend.module.outfit.entity.Outfit;
import cl.dressed.backend.module.outfit.entity.OutfitGarment;
import cl.dressed.backend.module.outfit.repository.OutfitGarmentRepository;
import cl.dressed.backend.module.outfit.repository.OutfitRepository;
import cl.dressed.backend.module.profile.entity.UserSize;
import cl.dressed.backend.module.profile.repository.UserSizeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Genera un outfit para el usuario basado en sus tallas guardadas en user_sizes.
 *
 * Lógica:
 * 1. Lee las tallas del usuario (top → S/M/L, bottom → M/L/XL, etc.)
 * 2. Por cada categoría de prenda (polera, jeans, etc.), busca en garments
 *    prendas que tengan esa talla disponible y estén en stock
 * 3. Selecciona una prenda aleatoria por categoría
 * 4. Guarda el outfit en outfits + outfit_garments
 * 5. Retorna el JSON con toda la info de las prendas (imágenes, links, etc.)
 *
 * Mapeo de categorías → tipo de talla del usuario:
 *   polera, camiseta, camisa, chaqueta, abrigo  → tipo "top"
 *   jeans, pantalon, falda, shorts               → tipo "bottom"
 *   zapatilla, zapato, bota                      → tipo "shoes"
 */
@Service
@RequiredArgsConstructor
public class OutfitGeneratorService {

    // Categorías soportadas y su tipo de talla correspondiente en user_sizes
    private static final Map<String, String> CATEGORY_TO_SIZE_TYPE = new LinkedHashMap<>();

    static {
    // top
    CATEGORY_TO_SIZE_TYPE.put("polera",                    "top");
    CATEGORY_TO_SIZE_TYPE.put("poleras",                   "top");
    CATEGORY_TO_SIZE_TYPE.put("poleras-y-tops",            "top");
    CATEGORY_TO_SIZE_TYPE.put("blusas-y-camisas",          "top");
    CATEGORY_TO_SIZE_TYPE.put("camisas",                   "top");
    CATEGORY_TO_SIZE_TYPE.put("basicos",                   "top");
    CATEGORY_TO_SIZE_TYPE.put("polerones",                 "top");
    CATEGORY_TO_SIZE_TYPE.put("sweaters-y-cardigans",      "top");
    CATEGORY_TO_SIZE_TYPE.put("blazers-y-chalecos-gilet",  "top");
    CATEGORY_TO_SIZE_TYPE.put("chaquetas-y-abrigos",       "top");
    CATEGORY_TO_SIZE_TYPE.put("ropa-deportiva",            "top");
    CATEGORY_TO_SIZE_TYPE.put("lino",                      "top");
    // bottom
    CATEGORY_TO_SIZE_TYPE.put("jeans",                     "bottom");
    CATEGORY_TO_SIZE_TYPE.put("pantalones",                "bottom");
    CATEGORY_TO_SIZE_TYPE.put("faldas",                    "bottom");
    CATEGORY_TO_SIZE_TYPE.put("shorts",                    "bottom");
    // full body (top como referencia)
    CATEGORY_TO_SIZE_TYPE.put("vestidos",                  "top");
    CATEGORY_TO_SIZE_TYPE.put("ropa-de-fiesta",            "top");
    CATEGORY_TO_SIZE_TYPE.put("ropa-formal",               "top");
}

    // Rol de cada categoría en el outfit (para el frontend)
    private static final Map<String, String> CATEGORY_TO_ROLE = new LinkedHashMap<>();
static {
    // top
    List.of("polera","poleras","poleras-y-tops","blusas-y-camisas","camisas",
            "basicos","polerones","sweaters-y-cardigans","blazers-y-chalecos-gilet",
            "chaquetas-y-abrigos","ropa-deportiva","lino","vestidos",
            "ropa-de-fiesta","ropa-formal")
        .forEach(c -> CATEGORY_TO_ROLE.put(c, "top"));
    // bottom
    List.of("jeans","pantalones","faldas","shorts")
        .forEach(c -> CATEGORY_TO_ROLE.put(c, "bottom"));
}

    private final GarmentRepository garmentRepository;
    private final OutfitRepository outfitRepository;
    private final OutfitGarmentRepository outfitGarmentRepository;
    private final UserSizeRepository userSizeRepository;

    @Transactional
    public OutfitResponseDTO generateOutfit(Long userId) {

        // 1. Obtener tallas del usuario: { "top" -> "M", "bottom" -> "L", ... }
        List<UserSize> userSizes = userSizeRepository.findByUserId(userId);
        if (userSizes.isEmpty()) {
            throw new IllegalStateException(
                "El usuario no tiene tallas registradas. Por favor completa tu perfil.");
        }

        Map<String, String> sizeByType = userSizes.stream()
            .collect(Collectors.toMap(
                us -> us.getType().toLowerCase(),
                us -> us.getValue().toUpperCase(),
                (a, b) -> a // si hay duplicados, tomar el primero
            ));

        // 2. Por cada categoría, buscar una prenda compatible
        List<OutfitGarmentItemDTO> selectedGarments = new ArrayList<>();
        Random random = new Random();

        // Categorías base del outfit: una de top y una de bottom como mínimo
        List<String> targetCategories = CATEGORY_TO_SIZE_TYPE.entrySet().stream()
        .filter(e -> sizeByType.containsKey(e.getValue()))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

        for (String category : targetCategories) {
            String sizeType = CATEGORY_TO_SIZE_TYPE.get(category);
            if (sizeType == null) continue;

            String userSize = sizeByType.get(sizeType);
            if (userSize == null) continue; // el usuario no tiene esta talla

            // Buscar prendas compatibles: categoría + talla + en stock
            List<Garment> compatible = garmentRepository
                .findByCategoryAndSizeContainingAndInStock(category, userSize, true);

            if (compatible.isEmpty()) continue;

            // Elegir una aleatoriamente
            Garment chosen = compatible.get(random.nextInt(compatible.size()));
            String role = CATEGORY_TO_ROLE.getOrDefault(category, category);

            selectedGarments.add(new OutfitGarmentItemDTO(
                chosen.getId(),
                chosen.getName(),
                chosen.getCategory(),
                role,
                chosen.getSize(),
                chosen.getImageUrl(),
                chosen.getProductLink(),
                chosen.getPrice(),
                chosen.getMainColor(),
                chosen.getFit(),
                chosen.getStyle()
            ));
        }

        if (selectedGarments.isEmpty()) {
            throw new IllegalStateException(
                "No se encontraron prendas compatibles con las tallas del usuario.");
        }

        // 3. Guardar outfit en BD
        Outfit outfit = outfitRepository.save(
            Outfit.builder().userId(userId).origin("automatic").build()
        );

        // 4. Guardar cada prenda del outfit
        for (OutfitGarmentItemDTO item : selectedGarments) {
            outfitGarmentRepository.save(
                OutfitGarment.builder()
                    .outfitId(outfit.getId())
                    .garmentId(item.garmentId())
                    .role(item.role())
                    .build()
            );
        }

        return new OutfitResponseDTO(
            outfit.getId(),
            userId,
            outfit.getGeneratedAt(),
            outfit.getOrigin(),
            selectedGarments
        );
    }

    /**
     * Retorna los últimos outfits generados para el usuario.
     */
    @Transactional(readOnly = true)
    public List<OutfitResponseDTO> getOutfitsForUser(Long userId) {
        List<Outfit> outfits = outfitRepository.findByUserIdOrderByGeneratedAtDesc(userId);

        return outfits.stream().map(outfit -> {
            List<OutfitGarment> outfitGarments =
                outfitGarmentRepository.findByOutfitId(outfit.getId());

            List<OutfitGarmentItemDTO> items = outfitGarments.stream()
                .map(og -> garmentRepository.findById(og.getGarmentId()).map(g ->
                    new OutfitGarmentItemDTO(
                        g.getId(), g.getName(), g.getCategory(), og.getRole(),
                        g.getSize(), g.getImageUrl(), g.getProductLink(),
                        g.getPrice(), g.getMainColor(), g.getFit(), g.getStyle()
                    )
                ).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

            return new OutfitResponseDTO(
                outfit.getId(), outfit.getUserId(),
                outfit.getGeneratedAt(), outfit.getOrigin(), items
            );
        }).collect(Collectors.toList());
    }
}