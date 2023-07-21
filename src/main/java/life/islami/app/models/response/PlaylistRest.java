package life.islami.app.models.response;

import java.util.Date;

import life.islami.app.enums.PrivacyType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PlaylistRest {
  private String playlistId;
  private String playlistName;
  private String description;
  private Date creationTime;
  private PrivacyType privacyType = PrivacyType.PRIVATE;
  private long numSongs;
}
