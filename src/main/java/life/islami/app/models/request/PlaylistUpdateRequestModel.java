package life.islami.app.models.request;

import life.islami.app.enums.PrivacyType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PlaylistUpdateRequestModel {
  private String playlistName;

  private String description;

  private PrivacyType privacyType;
}
