package life.islami.app.models.response;

import java.util.Date;

import life.islami.app.enums.AudioGenre;
import life.islami.app.enums.SongApprovalStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AudioRest {
  private String audioId;
  private String title;
  private String description;
  private String imageUrl;
  private String url;
  private AudioGenre genre;
  private Date creationTime;
  private String summary = null;
  private ArtistRest artist;
  private SongApprovalStatus approvalStatus;
  private long likeCount = 0;
  private long dislikeCount = 0;
  private long numComments = 0;

}
