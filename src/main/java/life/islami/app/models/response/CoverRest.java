package life.islami.app.models.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CoverRest {
  private String coverId;
  private String imageUrl;
  private String url;

}
