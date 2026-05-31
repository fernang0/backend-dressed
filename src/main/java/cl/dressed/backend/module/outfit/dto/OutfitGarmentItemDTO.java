package cl.dressed.backend.module.outfit.dto;

import java.math.BigDecimal;

public record OutfitGarmentItemDTO(
    Integer garmentId,
    String name,
    String category,
    String role,
    String size,
    String imageUrl,
    String productLink,
    BigDecimal price,
    String mainColor,
    String fit,
    String style
) {}