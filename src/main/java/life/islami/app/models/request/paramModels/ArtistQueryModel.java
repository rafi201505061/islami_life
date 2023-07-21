package life.islami.app.models.request.paramModels;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArtistQueryModel extends Pagination {
  private String artistName = "";
}
