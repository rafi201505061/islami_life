package life.islami.app.models.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import life.islami.app.enums.Reaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReactionCreationRequestModel {
  private Reaction reaction = null;

  @NotNull(message = "Audio id can not be null.")
  @NotBlank(message = "Audio id can not be blank.")
  private String audioId;

  @NotNull(message = "User id can not be null.")
  @NotBlank(message = "User id can not be blank.")
  private String userId;
}
