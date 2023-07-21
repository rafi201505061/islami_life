package life.islami.app.models.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import life.islami.app.enums.Reaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CommentReplyReactionCreationModel {
  private Reaction reaction = null;
  private long commentReplyId;

  @NotNull(message = "User id can not be null.")
  @NotBlank(message = "User id can not be blank.")
  private String userId;
}
