package life.islami.app.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import life.islami.app.data.entities.AudioEntity;
import life.islami.app.data.entities.CommentEntity;
import life.islami.app.data.entities.CommentReplyEntity;
import life.islami.app.data.entities.UserEntity;
import life.islami.app.data.repositories.AudioRepository;
import life.islami.app.data.repositories.CommentReplyRepository;
import life.islami.app.data.repositories.CommentRepository;
import life.islami.app.data.repositories.UserRepository;
import life.islami.app.dtos.CommentDto;
import life.islami.app.enums.Messages;
import life.islami.app.exceptions.CommonException;
import life.islami.app.models.request.paramModels.Pagination;
import life.islami.app.models.response.PaginatedResponse;
import life.islami.app.services.CommentService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {
  private final CommentRepository commentRepository;
  private final CommentReplyRepository commentReplyRepository;
  private final UserRepository userRepository;
  private final AudioRepository audioRepository;
  private final ModelMapper modelMapper;

  @Transactional
  @Override
  public CommentDto addComment(CommentDto commentDto) {
    UserEntity userEntity = userRepository.findByUserId(commentDto.getUserId());
    if (userEntity == null) {
      throw new CommonException(Messages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    AudioEntity audioEntity = audioRepository.findByAudioId(commentDto.getAudioId());
    if (audioEntity == null) {
      throw new CommonException(Messages.AUDIO_NOT_FOUND, HttpStatus.NOT_FOUND);
    }

    CommentEntity commentEntity = modelMapper.map(commentDto, CommentEntity.class);
    commentEntity.setAudio(audioEntity);
    commentEntity.setUser(userEntity);
    CommentEntity created = commentRepository.save(commentEntity);
    audioEntity.setNumComments(audioEntity.getNumComments() + 1);
    audioRepository.save(audioEntity);
    return modelMapper.map(created, CommentDto.class);
  }

  @Override
  public PaginatedResponse<CommentDto> getComments(String audioId, Pagination pagination) {
    Page<CommentEntity> commentPage = commentRepository.findByAudioAudioId(audioId,
        PageRequest.of(pagination.getPageNo(), pagination.getPageSize(), Sort.by("creationTime").descending()));
    PaginatedResponse<CommentDto> comments = new PaginatedResponse<>();
    comments.setPageNo(commentPage.getNumber());
    comments.setPageSize(commentPage.getSize());
    comments.setTotalRecords(commentPage.getTotalElements());
    List<CommentDto> commentDtos = new ArrayList<>();
    for (CommentEntity commentEntity : commentPage.getContent()) {
      commentDtos.add(modelMapper.map(commentEntity, CommentDto.class));
    }
    comments.setContent(commentDtos);
    return comments;
  }

  @Transactional
  @Override
  public void deleteComment(long id, String userId) {
    CommentEntity commentEntity = commentRepository.findByIdWithUser(id);
    if (commentEntity == null) {
      throw new CommonException(Messages.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    if (!commentEntity.getUser().getUserId().equals(userId)) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
    commentRepository.delete(commentEntity);

  }

  @Override
  public void deleteCommentReply(long id, String userId) {
    CommentReplyEntity commentReplyEntity = commentReplyRepository.findByIdWithUser(id);
    if (commentReplyEntity == null) {
      throw new CommonException(Messages.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    if (!commentReplyEntity.getUser().getUserId().equals(userId)) {
      throw new CommonException(Messages.FORBIDDEN, HttpStatus.FORBIDDEN);
    }
    commentReplyRepository.delete(commentReplyEntity);
  }

  @Transactional
  @Override
  public CommentDto addCommentReply(CommentDto commentDto) {
    CommentEntity commentEntity = commentRepository.findById(commentDto.getCommentId());
    if (commentEntity == null) {
      throw new CommonException(Messages.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    UserEntity userEntity = userRepository.findByUserId(commentDto.getUserId());
    if (userEntity == null) {
      throw new CommonException(Messages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    CommentReplyEntity commentReplyEntity = new CommentReplyEntity();
    commentReplyEntity.setCommentEntity(commentEntity);
    commentReplyEntity.setUser(userEntity);
    commentReplyEntity.setComment(commentDto.getComment());
    CommentReplyEntity created = commentReplyRepository.save(commentReplyEntity);
    commentEntity.setNumReplies(commentEntity.getNumReplies() + 1);
    return modelMapper.map(created, CommentDto.class);
  }

  @Override
  public PaginatedResponse<CommentDto> getCommentReplies(long commentId, Pagination pagination) {
    Page<CommentReplyEntity> commentPage = commentReplyRepository.findByCommentEntityId(commentId,
        PageRequest.of(pagination.getPageNo(), pagination.getPageSize(), Sort.by("creationTime").descending()));
    PaginatedResponse<CommentDto> comments = new PaginatedResponse<>();
    comments.setPageNo(commentPage.getNumber());
    comments.setPageSize(commentPage.getSize());
    comments.setTotalRecords(commentPage.getTotalElements());
    List<CommentDto> commentDtos = new ArrayList<>();
    for (CommentReplyEntity commentEntity : commentPage.getContent()) {
      commentDtos.add(modelMapper.map(commentEntity, CommentDto.class));
    }
    comments.setContent(commentDtos);
    return comments;
  }

  @Override
  public CommentDto updateComment(CommentDto commentDto) {
    CommentEntity commentEntity = commentRepository.findById(commentDto.getId());
    if (commentEntity == null) {
      throw new CommonException(Messages.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    commentEntity.setComment(commentDto.getComment());
    CommentEntity updated = commentRepository.save(commentEntity);
    return modelMapper.map(updated, CommentDto.class);
  }

  @Override
  public CommentDto updateCommentReply(CommentDto commentDto) {
    CommentReplyEntity commentReplyEntity = commentReplyRepository.findByIdWithUser(commentDto.getId());
    if (commentReplyEntity == null) {
      throw new CommonException(Messages.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    commentReplyEntity.setComment(commentDto.getComment());
    CommentReplyEntity updated = commentReplyRepository.save(commentReplyEntity);
    return modelMapper.map(updated, CommentDto.class);
  }

}
