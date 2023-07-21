package life.islami.app.models.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentReplyCreationModel {
  @NotNull(message = "User id can not be null.")
  @NotBlank(message = "User id can not be blank.")
  private String userId;

  @NotNull(message = "Comment id can not be null.")
  @NotBlank(message = "Comment id can not be blank.")
  private String commentId;

  @NotNull(message = "Comment can not be null.")
  @NotBlank(message = "Comment can not be blank.")
  @Size(max = 1000, message = "Comment length can not be more than 1000 characters.")
  private String comment;
}
