package life.islami.app.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import life.islami.app.data.entities.CoverEntity;

public interface CoverRepository extends JpaRepository<CoverEntity, Long> {
  CoverEntity findByCoverId(String coverId);
}
