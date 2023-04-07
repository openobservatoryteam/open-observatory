package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.UserEntity;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByUsernameIgnoreCase(String username);

  Set<UserEntity>
      findAllByNotificationsEnabledIsTrueAndLatitudeIsNotNullAndLongitudeIsNotNullAndLastPositionUpdateIsGreaterThanEqual(
          Instant timestamp);

  Optional<UserEntity> findByUsernameIgnoreCase(String username);
}
