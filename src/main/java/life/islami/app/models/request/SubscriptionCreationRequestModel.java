package life.islami.app.models.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubscriptionCreationRequestModel {
  @Positive(message = "Duration must be positive integer")
  private long duration;
  @Positive(message = "Price must be positive")
  private double price;

  @NotBlank(message = "title can not be blank")
  @Size(min = 10, max = 250, message = "title length must be between 10 to 250 characters")
  private String title;
}
