package life.islami.app.models.response;

import life.islami.app.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserRest extends MinimizedUserRest {
  private String userName;
  private String phoneNo;
  private String email;
  private Role role;
}
