package life.islami.app.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionDto {
  private String title;
  private String subscriptionId;
  private double price;
  private int duration;

}
