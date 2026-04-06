package cl.dressed.backend.module.admin.dto;

public final class AdminDto {

    private AdminDto() {
    }

    public record AdminStatusResponse(
            String status,
            String message
    ) {
    }
}
