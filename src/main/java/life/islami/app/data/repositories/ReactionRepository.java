package life.islami.app.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import life.islami.app.data.entities.ReactionEntity;

public interface ReactionRepository extends JpaRepository<ReactionEntity, Long> {
  ReactionEntity findByUserUserIdAndAudioAudioId(String userId, String audioId);
}
