package cl.dressed.backend.module.admin.dto;

import java.time.LocalDateTime;
import java.util.List;

public record AdminUserDTO(
    Long id,
    String email,
    Boolean active,
    String oauthProvider,
    List<String> roles,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}