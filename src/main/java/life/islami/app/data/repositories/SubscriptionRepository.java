package life.islami.app.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import life.islami.app.data.entities.SubscriptionEntity;

public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Long> {
  List<SubscriptionEntity> findAllByActive(boolean active);

  SubscriptionEntity findBySubscriptionId(String subscriptionId);
}
