package life.islami.app.models.request.paramModels;

import javax.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pagination {
  @PositiveOrZero(message = "Page no. can not be negative")
  private int pageNo = 0;

  @PositiveOrZero(message = "Page size can not be negative")
  private int pageSize = 15;

}
