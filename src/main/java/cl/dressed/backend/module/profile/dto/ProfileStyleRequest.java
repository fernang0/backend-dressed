package cl.dressed.backend.module.profile.dto;

import jakarta.validation.constraints.NotNull;
import java.util.Set;

public record ProfileStyleRequest(
        @NotNull(message = "styles no puede ser null")
        Set<String> styles
) {}