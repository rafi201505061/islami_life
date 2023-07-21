package life.islami.app.models.request.paramModels;

import life.islami.app.enums.AudioGenre;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AudioQueryModel extends Pagination {
  private AudioGenre genre = null;
}
