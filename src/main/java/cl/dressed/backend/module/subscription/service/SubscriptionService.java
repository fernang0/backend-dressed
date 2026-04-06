package cl.dressed.backend.module.subscription.service;

import cl.dressed.backend.module.subscription.dto.SubscriptionDto;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {

    public SubscriptionDto.SubscriptionResponse findById(Long id) {
        return new SubscriptionDto.SubscriptionResponse(id, "MONTHLY", LocalDate.now().plusMonths(1));
    }
}
