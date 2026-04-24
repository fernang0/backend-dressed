package cl.dressed.backend.module.profile.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public class UserMeasurementDto {

    public record UserMeasurementRequest(

        @DecimalMin(value = "100.0", message = "La altura mínima es 100 cm")
        @DecimalMax(value = "230.0", message = "La altura máxima es 230 cm")
        BigDecimal heightCm,

        @DecimalMin(value = "30.0", message = "El valor mínimo es 30 cm")
        @DecimalMax(value = "200.0", message = "El valor máximo es 200 cm")
        BigDecimal shouldersCm,

        @DecimalMin(value = "30.0", message = "El valor mínimo es 30 cm")
        @DecimalMax(value = "200.0", message = "El valor máximo es 200 cm")
        BigDecimal chestCm,

        @DecimalMin(value = "30.0", message = "El valor mínimo es 30 cm")
        @DecimalMax(value = "200.0", message = "El valor máximo es 200 cm")
        BigDecimal waistCm,

        @DecimalMin(value = "30.0", message = "El valor mínimo es 30 cm")
        @DecimalMax(value = "200.0", message = "El valor máximo es 200 cm")
        BigDecimal hipsCm,

        @DecimalMin(value = "20.0", message = "El valor mínimo es 20 cm")
        @DecimalMax(value = "120.0", message = "El valor máximo es 120 cm")
        BigDecimal torsoLengthCm,

        @DecimalMin(value = "30.0", message = "El valor mínimo es 30 cm")
        @DecimalMax(value = "150.0", message = "El valor máximo es 150 cm")
        BigDecimal legLengthCm

    ) {}

    public record UserMeasurementResponse(
        BigDecimal heightCm,
        BigDecimal shouldersCm,
        BigDecimal chestCm,
        BigDecimal waistCm,
        BigDecimal hipsCm,
        BigDecimal torsoLengthCm,
        BigDecimal legLengthCm
    ) {}
    
}
