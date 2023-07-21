package life.islami.app.models.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MinimizedUserRest {
  private String userId;
  private String fullName;
  private String profilePictureUrl;

}
