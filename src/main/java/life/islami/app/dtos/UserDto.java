package life.islami.app.dtos;

import life.islami.app.enums.Role;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class UserDto {
  private long id;
  private String userId;
  private String fullName;
  private String userName;
  private String phoneNo;
  private String email;
  private String password;
  private String retypedPassword;
  private String encryptedPassword;
  private String profilePictureUrl;
  private Role role;
  private String passwordResetToken;
  private String currentPassword;
  private boolean subscribed;

  @Override
  public String toString() {
    return "UserDto [id=" + id + ", userId=" + userId + ", userName=" + userName + ", phoneNo=" + phoneNo + ", email="
        + email + ", password=" + password + ", retypedPassword=" + retypedPassword + ", encryptedPassword="
        + encryptedPassword + ", profilePictureUrl=" + profilePictureUrl + ", role=" + role + "]";
  }

}
