package cl.dressed.backend.module.profile.dto;

import java.util.Set;

public record ProfileStyleResponse(
        Set<String> styles
) {}