package life.islami.app.models.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionRest {
  private String title;
  private String subscriptionId;
  private double price;
  private int duration;
}
