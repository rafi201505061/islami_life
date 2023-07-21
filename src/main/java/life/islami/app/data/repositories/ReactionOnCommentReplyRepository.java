package life.islami.app.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import life.islami.app.data.entities.ReactionOnCommentReplyEntity;

public interface ReactionOnCommentReplyRepository extends JpaRepository<ReactionOnCommentReplyEntity, Long> {
  ReactionOnCommentReplyEntity findByUserUserIdAndCommentReplyId(String userId, long id);
}
