package life.islami.app.models.request;

import org.springframework.web.multipart.MultipartFile;

import life.islami.app.enums.AudioGenre;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AudioUpdateRequestModel {
  private String title;
  private String description;
  private String summary;
  private AudioGenre genre;
  private String artistId;
  private MultipartFile image;
}
