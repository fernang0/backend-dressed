package cl.dressed.backend.module.profile.dto;

import java.util.List;

public record ProfileCompletenessResponse(
        int percentage,
        List<String> missing,
        String message
) {}