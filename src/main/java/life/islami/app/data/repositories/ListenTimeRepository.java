package life.islami.app.data.repositories;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;

import life.islami.app.data.entities.ListenTimeEntity;

public interface ListenTimeRepository
    extends PagingAndSortingRepository<ListenTimeEntity, Long>, JpaSpecificationExecutor<ListenTimeEntity> {

}
