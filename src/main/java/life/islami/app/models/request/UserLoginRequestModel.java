package life.islami.app.models.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserLoginRequestModel {
  @NotBlank(message = "Username can not be blank.")
  @Size(min = 1, max = 250, message = "Username must be between 1 to 250 characters")
  private String userName;

  @NotBlank(message = "Current password can not be empty.")
  @Size(min = 6, max = 20, message = "Current password must be between 6 to 20 characters")
  private String password;

  @Override
  public String toString() {
    return "UserLoginRequestModel [userName=" + userName + ", password=" + password + "]";
  }
}
