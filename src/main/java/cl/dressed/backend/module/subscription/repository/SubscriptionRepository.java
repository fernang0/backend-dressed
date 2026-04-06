package cl.dressed.backend.module.subscription.repository;

import cl.dressed.backend.module.subscription.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
