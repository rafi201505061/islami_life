package life.islami.app.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import life.islami.app.data.entities.CommentReplyEntity;

public interface CommentReplyRepository extends PagingAndSortingRepository<CommentReplyEntity, Long> {
  CommentReplyEntity findById(long id);

  @Query("SELECT cre FROM CommentReplyEntity cre JOIN FETCH cre.user WHERE cre.id=:id")
  CommentReplyEntity findByIdWithUser(@Param("id") long id);

  Page<CommentReplyEntity> findByCommentEntityId(long id, Pageable pageable);

}
