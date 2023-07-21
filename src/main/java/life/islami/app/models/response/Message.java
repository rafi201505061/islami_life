package life.islami.app.models.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
  private String code = "INTERNAL_SERVER_ERROR";
  private String message = "Something went wrong. Please try again later.";
}
