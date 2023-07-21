package life.islami.app.models.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordFields {
  @NotBlank(message = "Password is required")
  @Size(min = 6, max = 20, message = "Password must be between 6 to 20 characters")
  private String password;

  @NotBlank(message = "Retyped password is required")
  @Size(min = 6, max = 20, message = "Retyped password must be between 6 to 20 characters")
  private String retypedPassword;
}
