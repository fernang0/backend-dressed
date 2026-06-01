package cl.dressed.backend.module.outfit.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OutfitResponseDTO(
    Long outfitId,
    Long userId,
    LocalDateTime generatedAt,
    String origin,
    List<OutfitGarmentItemDTO> garments
) {}