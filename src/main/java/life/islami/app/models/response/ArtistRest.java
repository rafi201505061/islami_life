package life.islami.app.models.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ArtistRest {
  private String artistId;
  private String artistName;
  private String profileImageUrl;
}
