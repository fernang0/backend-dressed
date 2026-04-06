package cl.dressed.backend.module.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public final class CatalogDto {

    private CatalogDto() {
    }

    public record ProductRequest(
            @NotBlank String name,
            @Positive BigDecimal price
    ) {
    }

    public record ProductResponse(
            Long id,
            String name,
            BigDecimal price
    ) {
    }
}
