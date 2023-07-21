package life.islami.app.data.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import life.islami.app.data.entities.UserSubscriptionHistory;

public interface UserSubscriptionHistoryRepository extends JpaRepository<UserSubscriptionHistory, Long> {
  List<UserSubscriptionHistory> findAllByActive(boolean active);

  Long countByActiveAndUserId(boolean active, String userId);
}
