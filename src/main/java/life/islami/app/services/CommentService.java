package life.islami.app.services;

import life.islami.app.dtos.CommentDto;
import life.islami.app.models.request.paramModels.Pagination;
import life.islami.app.models.response.PaginatedResponse;

public interface CommentService {
  public CommentDto addComment(CommentDto commentDto);

  public CommentDto updateComment(CommentDto commentDto);

  public CommentDto addCommentReply(CommentDto commentDto);

  public CommentDto updateCommentReply(CommentDto commentDto);

  public void deleteComment(long id, String userId);

  public void deleteCommentReply(long id, String userId);

  PaginatedResponse<CommentDto> getComments(String audioId, Pagination pagination);

  PaginatedResponse<CommentDto> getCommentReplies(long commentId, Pagination pagination);

}
