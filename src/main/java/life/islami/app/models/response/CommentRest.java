package life.islami.app.models.response;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CommentRest {
  private long id;
  private String comment;
  private MinimizedUserRest user;
  private long likeCount = 0;
  private long dislikeCount = 0;
  private long numReplies = 0;
  private Date creationTime;
}
