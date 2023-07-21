package life.islami.app.models.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserPasswordUpdateRequestModel extends PasswordFields {

  @NotBlank(message = "Current password can not be empty.")
  @Size(min = 6, max = 20, message = "Current password must be between 6 to 20 characters")
  private String currentPassword;
}
