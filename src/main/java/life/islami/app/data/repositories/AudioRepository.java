package life.islami.app.data.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import life.islami.app.data.entities.AudioEntity;

public interface AudioRepository
    extends PagingAndSortingRepository<AudioEntity, Long>, JpaSpecificationExecutor<AudioEntity> {
  @Query(value = "SELECT e FROM AudioEntity e WHERE e.deleted=false")
  AudioEntity findByAudioId(String audioId);

  Page<AudioEntity> findAllByArtist_ArtistId(String artistId, Pageable pageable);
}
