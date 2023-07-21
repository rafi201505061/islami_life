package life.islami.app.services;

import life.islami.app.dtos.ReactionDto;
import life.islami.app.enums.Reaction;

public interface ReactionService {
  public void addReaction(ReactionDto reactionDto);

  public void addReactionToComment(ReactionDto reactionDto);

  public void addReactionToCommentReply(ReactionDto reactionDto);

  public Reaction getReaction(String audioId, String userId);

}
