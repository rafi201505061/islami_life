package life.islami.app.data.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import life.islami.app.data.entities.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
  UserEntity findByUserId(String userId);

  UserEntity findByUserName(String userId);

}
