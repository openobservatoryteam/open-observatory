package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.AchievementEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AchievementRepository extends JpaRepository<AchievementEntity, Long> {
  Optional<AchievementEntity> findAllByTitle(String title);
}
