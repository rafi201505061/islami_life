package life.islami.app.services;

import java.util.List;

import life.islami.app.dtos.SubscriptionDto;

public interface SubscriptionService {
  SubscriptionDto createSubscription(SubscriptionDto subscriptionDto);

  List<SubscriptionDto> retrieveSubscriptions();

  void deleteSubscription(String subscriptionId);
}
