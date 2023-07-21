package life.islami.app.dtos;

import life.islami.app.enums.Reaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReactionDto {
  private String audioId;
  private long commentId;
  private long commentReplyId;
  private String userId;
  private Reaction reaction;

  @Override
  public String toString() {
    return "ReactionDto [audioId=" + audioId + ", userId=" + userId + ", reaction=" + reaction + "]";
  }
}
