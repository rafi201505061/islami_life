package life.islami.app.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import life.islami.app.data.entities.PlaylistEntity;
import life.islami.app.enums.PrivacyType;

public interface PlaylistRepository extends PagingAndSortingRepository<PlaylistEntity, Long> {
  PlaylistEntity findByPlaylistIdAndIsActive(String playlistId, boolean isActive);

  Page<PlaylistEntity> findByOwnerUserIdAndIsActive(String userId, boolean isActive, Pageable pageable);

  Page<PlaylistEntity> findByOwnerUserIdAndPrivacyTypeAndIsActive(String userId, PrivacyType privacyType,
      boolean isActive,
      Pageable pageable);

}
