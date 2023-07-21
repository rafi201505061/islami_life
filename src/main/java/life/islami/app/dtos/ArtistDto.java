package life.islami.app.dtos;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class ArtistDto {
  private String artistId;
  private String artistName;
  private String profileImageUrl;
  private MultipartFile profileImage;
}
