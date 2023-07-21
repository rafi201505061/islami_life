package life.islami.app.dtos;

import java.util.Date;

import life.islami.app.enums.PrivacyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PlaylistDto {
  private String playlistId;
  private String playlistName;
  private String description;
  private String ownerId;
  private Date creationTime;
  private PrivacyType privacyType = PrivacyType.PRIVATE;
  private long numSongs;
}
