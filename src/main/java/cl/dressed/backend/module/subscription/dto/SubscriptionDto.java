package cl.dressed.backend.module.subscription.dto;

import java.time.LocalDate;

public final class SubscriptionDto {

    private SubscriptionDto() {
    }

    public record SubscriptionResponse(
            Long id,
            String plan,
            LocalDate nextBillingDate
    ) {
    }
}
