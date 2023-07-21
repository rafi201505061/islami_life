package life.islami.app.dtos;

import java.util.Date;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class CommentDto {
  private long id;
  private long commentId;
  private String userId;
  private String audioId;
  private String comment;
  private UserDto user;
  private long likeCount = 0;
  private long dislikeCount = 0;
  private long numReplies = 0;
  private Date creationTime;
}
