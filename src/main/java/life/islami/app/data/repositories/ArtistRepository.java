package life.islami.app.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import life.islami.app.data.entities.ArtistEntity;

public interface ArtistRepository
    extends PagingAndSortingRepository<ArtistEntity, Long>, JpaSpecificationExecutor<ArtistEntity> {
  ArtistEntity findByArtistId(String artistId);
}
