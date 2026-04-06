package cl.dressed.backend.module.subscription.controller;

import cl.dressed.backend.module.subscription.dto.SubscriptionDto;
import cl.dressed.backend.module.subscription.service.SubscriptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionDto.SubscriptionResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(subscriptionService.findById(id));
    }
}
