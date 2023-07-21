package life.islami.app.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import life.islami.app.data.entities.CommentEntity;

public interface CommentRepository extends PagingAndSortingRepository<CommentEntity, Long> {
  @Query(value = "SELECT ce FROM CommentEntity ce JOIN FETCH ce.user ceu WHERE ce.audio.audioId =:audioId", countQuery = "SELECT COUNT(ce) FROM CommentEntity ce WHERE ce.audio.audioId = :audioId")
  Page<CommentEntity> findByAudioAudioId(@Param("audioId") String audioId, Pageable pageable);

  @Query("SELECT ce from CommentEntity ce JOIN FETCH ce.user WHERE ce.id=:id")
  CommentEntity findByIdWithUser(@Param("id") long id);

  CommentEntity findById(long id);
}
