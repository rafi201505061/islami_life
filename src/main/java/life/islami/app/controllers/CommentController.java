package life.islami.app.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import life.islami.app.dtos.CommentDto;
import life.islami.app.dtos.ReactionDto;
import life.islami.app.enums.Messages;
import life.islami.app.exceptions.CommonException;
import life.islami.app.models.request.CommentReactionCreationModel;
import life.islami.app.models.request.CommentReplyCreationModel;
import life.islami.app.models.request.CommentReplyReactionCreationModel;
import life.islami.app.models.request.paramModels.Pagination;
import life.islami.app.models.response.CommentRest;
import life.islami.app.models.response.PaginatedResponse;
import life.islami.app.services.CommentService;
import life.islami.app.services.ReactionService;
import life.islami.app.utils.CommonUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/comments")
public class CommentController {
  private final CommonUtils utils;
  private final ReactionService reactionService;
  private final CommentService commentService;
  private final ModelMapper modelMapper;

  @DeleteMapping("/{commentId}")
  public ResponseEntity<String> deleteComment(@PathVariable long commentId, Principal principal) {
    commentService.deleteComment(commentId, principal.getName());
    return new ResponseEntity<String>("comment deleted successfully", null, HttpStatus.OK);
  }

  @PutMapping("/{commentId}")
  public CommentRest updateComment(@PathVariable long commentId, Principal principal,
      @RequestParam("comment") String comment) {
    return modelMapper.map(commentService.updateComment(new CommentDto().setId(commentId).setComment(comment)),
        CommentRest.class);
  }

  @PutMapping("/{commentId}/reactions")
  public ResponseEntity<String> addReaction(@PathVariable long commentId, Principal principal,
      @RequestBody CommentReactionCreationModel commentReactionCreationModel, Errors errors) {
    utils.handleValidationErrors(errors);
    if (commentReactionCreationModel.getCommentId() != commentId) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.BAD_REQUEST);
    }
    if (!principal.getName().equals(commentReactionCreationModel.getUserId())) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }

    reactionService.addReactionToComment(modelMapper.map(commentReactionCreationModel, ReactionDto.class));

    return new ResponseEntity<String>("reaction added successfully", null, HttpStatus.OK);

  }

  @PostMapping("/{commentId}/replies")
  public CommentRest addCommentReply(@PathVariable long commentId, Principal principal,
      @RequestBody CommentReplyCreationModel commentReplyCreationModel) {
    return modelMapper.map(commentService.addCommentReply(modelMapper.map(commentReplyCreationModel, CommentDto.class)),
        CommentRest.class);
  }

  @GetMapping("/{commentId}/replies")
  public PaginatedResponse<CommentRest> getCommentReplies(@PathVariable long commentId,
      @ModelAttribute Pagination pagination) {
    PaginatedResponse<CommentDto> commentDtos = commentService.getCommentReplies(commentId, pagination);
    PaginatedResponse<CommentRest> commentRests = new PaginatedResponse<>();
    List<CommentRest> comments = new ArrayList<>();
    for (CommentDto commentDto : commentDtos.getContent()) {
      comments.add(modelMapper.map(commentDto, CommentRest.class));
    }
    commentRests.setContent(comments);
    return commentRests;
  }

  @DeleteMapping("/replies/{replyId}")
  public ResponseEntity<String> deleteCommentReply(@PathVariable long replyId, Principal principal) {
    commentService.deleteCommentReply(replyId, principal.getName());
    return new ResponseEntity<String>("comment deleted successfully", null, HttpStatus.OK);
  }

  @PutMapping("/replies/{replyId}")
  public CommentRest updateCommentReply(@PathVariable long replyId, Principal principal,
      @RequestParam("comment") String comment) {
    return modelMapper.map(commentService.updateCommentReply(new CommentDto().setId(replyId).setComment(comment)),
        CommentRest.class);
  }

  @PutMapping("/replies/{replyId}/reactions")
  public ResponseEntity<String> addReactionToCommentReply(@PathVariable(name = "replyId") long replyId,
      Principal principal,
      @RequestBody CommentReplyReactionCreationModel commentReplyReactionCreationModel, Errors errors) {
    utils.handleValidationErrors(errors);
    if (commentReplyReactionCreationModel.getCommentReplyId() != replyId) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.BAD_REQUEST);
    }
    if (!principal.getName().equals(commentReplyReactionCreationModel.getUserId())) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }

    reactionService.addReactionToCommentReply(modelMapper.map(commentReplyReactionCreationModel, ReactionDto.class));

    return new ResponseEntity<String>("reaction added successfully", null, HttpStatus.OK);

  }
}
