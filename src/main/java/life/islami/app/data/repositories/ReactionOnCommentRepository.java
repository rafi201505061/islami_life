package life.islami.app.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import life.islami.app.data.entities.ReactionOnCommentEntity;

public interface ReactionOnCommentRepository extends JpaRepository<ReactionOnCommentEntity, Long> {
  ReactionOnCommentEntity findByUserUserIdAndCommentId(String userId, long id);
}
