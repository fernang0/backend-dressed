package cl.dressed.backend.shared.dto;

public record ContactRequest(
    String name,
    String email,
    String message
) {}