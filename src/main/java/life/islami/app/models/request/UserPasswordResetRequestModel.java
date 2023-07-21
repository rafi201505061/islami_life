package life.islami.app.models.request;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class UserPasswordResetRequestModel extends PasswordFields {
  @NotBlank(message = "Password reset token can't be blank")
  private String passwordResetToken;
}
