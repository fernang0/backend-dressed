package cl.dressed.backend.module.profile.dto;

public class UserSizeDto {

    public record UserSizeUpdateRequest(
        String top,
        String bottom,
        String shoes
    ) {}

    public record UserSizeResponse(
        String top,
        String bottom,
        String shoes
    ) {}
}