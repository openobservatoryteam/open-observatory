package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

  boolean existsByUsernameIgnoreCase(String username);

  Optional<UserEntity> findByUsernameIgnoreCase(String username);
}
