package cl.dressed.backend.module.profile.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

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

    public record BodyMeasurementsRequest(
        @NotNull
        @Min(100)
        @Max(230)
        Integer altura,

        @NotNull
        @Min(1)
        Integer pecho,  

        @NotNull
        @Min(1)
        Integer cintura,

        @NotNull
        @Min(1)
        Integer cadera
    ) {}
}