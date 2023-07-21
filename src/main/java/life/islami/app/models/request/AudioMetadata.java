package life.islami.app.models.request;

import life.islami.app.enums.AudioGenre;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AudioMetadata {
  private String title;
  private String description;
  private AudioGenre genre;
  private String artistId;

}
