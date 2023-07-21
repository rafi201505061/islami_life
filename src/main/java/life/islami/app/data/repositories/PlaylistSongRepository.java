package life.islami.app.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import life.islami.app.data.entities.PlaylistSongEntity;

public interface PlaylistSongRepository extends PagingAndSortingRepository<PlaylistSongEntity, Long> {
  Page<PlaylistSongEntity> findByPlaylistPlaylistId(String playlistId, Pageable pageable);

  PlaylistSongEntity findByPlaylistPlaylistIdAndAudioAudioId(String playlistId, String audioId);
}
