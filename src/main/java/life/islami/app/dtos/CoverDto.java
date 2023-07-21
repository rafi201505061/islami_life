package life.islami.app.dtos;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoverDto {
  private String coverId;
  private MultipartFile image;
  private String imageUrl;
  private String url;

  public CoverDto(MultipartFile image, String url) {
    this.image = image;
    this.url = url;
  }
}
