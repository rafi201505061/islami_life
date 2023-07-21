package life.islami.app.services.impl;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import life.islami.app.data.entities.AudioEntity;
import life.islami.app.data.entities.CommentEntity;
import life.islami.app.data.entities.CommentReplyEntity;
import life.islami.app.data.entities.ReactionEntity;
import life.islami.app.data.entities.ReactionOnCommentEntity;
import life.islami.app.data.entities.ReactionOnCommentReplyEntity;
import life.islami.app.data.entities.UserEntity;
import life.islami.app.data.repositories.AudioRepository;
import life.islami.app.data.repositories.CommentReplyRepository;
import life.islami.app.data.repositories.CommentRepository;
import life.islami.app.data.repositories.ReactionOnCommentReplyRepository;
import life.islami.app.data.repositories.ReactionOnCommentRepository;
import life.islami.app.data.repositories.ReactionRepository;
import life.islami.app.data.repositories.UserRepository;
import life.islami.app.dtos.ReactionDto;
import life.islami.app.enums.Messages;
import life.islami.app.enums.Reaction;
import life.islami.app.exceptions.CommonException;
import life.islami.app.services.ReactionService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReactionServiceImpl implements ReactionService {
  private final UserRepository userRepository;
  private final AudioRepository audioRepository;
  private final ReactionRepository reactionRepository;
  private final CommentRepository commentRepository;
  private final CommentReplyRepository commentReplyRepository;
  private final ReactionOnCommentRepository reactionOnCommentRepository;
  private final ReactionOnCommentReplyRepository reactionOnCommentReplyRepository;

  @Transactional
  @Override
  public void addReaction(ReactionDto reactionDto) {
    ReactionEntity reactionEntity = reactionRepository.findByUserUserIdAndAudioAudioId(reactionDto.getUserId(),
        reactionDto.getAudioId());
    AudioEntity audioEntity = audioRepository.findByAudioId(reactionDto.getAudioId());
    if (audioEntity == null) {
      throw new CommonException(Messages.AUDIO_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    if (reactionEntity == null) {
      reactionEntity = new ReactionEntity();
      UserEntity userEntity = userRepository.findByUserId(reactionDto.getUserId());
      if (userEntity == null) {
        throw new CommonException(Messages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
      }
      reactionEntity.setAudio(audioEntity);
      reactionEntity.setUser(userEntity);
    }

    long likeCount = audioEntity.getLikeCount();
    long dislikeCount = audioEntity.getDislikeCount();
    if (reactionDto.getReaction() == reactionEntity.getReaction()) {
      return;
    }
    if (reactionEntity.getReaction() == null) {
      if (reactionDto.getReaction() == Reaction.LIKE) {
        likeCount++;
      } else if (reactionDto.getReaction() == Reaction.DISLIKE) {
        dislikeCount++;
      }
    } else if (reactionDto.getReaction() == null) {
      if (reactionEntity.getReaction() == Reaction.LIKE) {
        likeCount--;
      } else if (reactionEntity.getReaction() == Reaction.DISLIKE) {
        dislikeCount--;
      }
    } else {
      if (reactionDto.getReaction() == Reaction.LIKE) {
        likeCount++;
        dislikeCount--;
      } else {
        likeCount--;
        dislikeCount++;
      }
    }

    reactionEntity.setReaction(reactionDto.getReaction());
    reactionRepository.save(reactionEntity);
    audioEntity.setLikeCount(likeCount);
    audioEntity.setDislikeCount(dislikeCount);
    audioRepository.save(audioEntity);
  }

  @Override
  public Reaction getReaction(String audioId, String userId) {
    ReactionEntity reactionEntity = reactionRepository.findByUserUserIdAndAudioAudioId(userId,
        audioId);
    if (reactionEntity == null) {
      return null;
    }
    return reactionEntity.getReaction();
  }

  @Override
  public void addReactionToComment(ReactionDto reactionDto) {
    ReactionOnCommentEntity reactionEntity = reactionOnCommentRepository.findByUserUserIdAndCommentId(
        reactionDto.getUserId(),
        reactionDto.getCommentId());
    CommentEntity commentEntity = commentRepository.findById(reactionDto.getCommentId());
    if (commentEntity == null) {
      throw new CommonException(Messages.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    if (reactionEntity == null) {
      reactionEntity = new ReactionOnCommentEntity();
      UserEntity userEntity = userRepository.findByUserId(reactionDto.getUserId());
      if (userEntity == null) {
        throw new CommonException(Messages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
      }
      reactionEntity.setComment(commentEntity);
      reactionEntity.setUser(userEntity);
    }

    long likeCount = commentEntity.getLikeCount();
    long dislikeCount = commentEntity.getDislikeCount();
    if (reactionDto.getReaction() == reactionEntity.getReaction()) {
      return;
    }
    if (reactionEntity.getReaction() == null) {
      if (reactionDto.getReaction() == Reaction.LIKE) {
        likeCount++;
      } else if (reactionDto.getReaction() == Reaction.DISLIKE) {
        dislikeCount++;
      }
    } else if (reactionDto.getReaction() == null) {
      if (reactionEntity.getReaction() == Reaction.LIKE) {
        likeCount--;
      } else if (reactionEntity.getReaction() == Reaction.DISLIKE) {
        dislikeCount--;
      }
    } else {
      if (reactionDto.getReaction() == Reaction.LIKE) {
        likeCount++;
        dislikeCount--;
      } else {
        likeCount--;
        dislikeCount++;
      }
    }

    reactionEntity.setReaction(reactionDto.getReaction());
    reactionOnCommentRepository.save(reactionEntity);
    commentEntity.setLikeCount(likeCount);
    commentEntity.setDislikeCount(dislikeCount);
    commentRepository.save(commentEntity);
  }

  @Override
  public void addReactionToCommentReply(ReactionDto reactionDto) {
    ReactionOnCommentReplyEntity reactionEntity = reactionOnCommentReplyRepository.findByUserUserIdAndCommentReplyId(
        reactionDto.getUserId(),
        reactionDto.getCommentReplyId());
    CommentReplyEntity commentReplyEntity = commentReplyRepository.findById(reactionDto.getCommentReplyId());
    if (commentReplyEntity == null) {
      throw new CommonException(Messages.COMMENT_NOT_FOUND, HttpStatus.NOT_FOUND);
    }
    if (reactionEntity == null) {
      reactionEntity = new ReactionOnCommentReplyEntity();
      UserEntity userEntity = userRepository.findByUserId(reactionDto.getUserId());
      if (userEntity == null) {
        throw new CommonException(Messages.USER_NOT_FOUND, HttpStatus.NOT_FOUND);
      }
      reactionEntity.setCommentReply(commentReplyEntity);
      reactionEntity.setUser(userEntity);
    }

    long likeCount = commentReplyEntity.getLikeCount();
    long dislikeCount = commentReplyEntity.getDislikeCount();
    if (reactionDto.getReaction() == reactionEntity.getReaction()) {
      return;
    }
    if (reactionEntity.getReaction() == null) {
      if (reactionDto.getReaction() == Reaction.LIKE) {
        likeCount++;
      } else if (reactionDto.getReaction() == Reaction.DISLIKE) {
        dislikeCount++;
      }
    } else if (reactionDto.getReaction() == null) {
      if (reactionEntity.getReaction() == Reaction.LIKE) {
        likeCount--;
      } else if (reactionEntity.getReaction() == Reaction.DISLIKE) {
        dislikeCount--;
      }
    } else {
      if (reactionDto.getReaction() == Reaction.LIKE) {
        likeCount++;
        dislikeCount--;
      } else {
        likeCount--;
        dislikeCount++;
      }
    }

    reactionEntity.setReaction(reactionDto.getReaction());
    reactionOnCommentReplyRepository.save(reactionEntity);
    commentReplyEntity.setLikeCount(likeCount);
    commentReplyEntity.setDislikeCount(dislikeCount);
    commentReplyRepository.save(commentReplyEntity);
  }
}
