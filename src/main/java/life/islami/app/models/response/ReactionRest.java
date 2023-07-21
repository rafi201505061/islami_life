package life.islami.app.models.response;

import life.islami.app.enums.Reaction;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@NoArgsConstructor
public class ReactionRest {
  private Reaction reaction;
}
