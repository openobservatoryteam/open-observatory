package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.AchievementEntity;
import fr.openobservatory.backend.entities.UserAchievementEntity;
import fr.openobservatory.backend.entities.UserEntity;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievementEntity, Long> {
  Set<UserAchievementEntity> findAllByUser(UserEntity user);

  Optional<UserAchievementEntity> findAllByUserAndAchievement(
      UserEntity user, AchievementEntity achievement);
}
