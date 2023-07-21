package life.islami.app.models.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserCreationRequestModel extends PasswordFields {
  @NotBlank(message = "Username can not be blank.")
  @Size(min = 1, max = 250, message = "Username must be between 1 to 250 characters")
  private String userName;

  @NotBlank(message = "Full name can not be blank.")
  @Size(min = 1, max = 200, message = "Full name must be between 1 to 200 characters")
  private String fullName;

}
