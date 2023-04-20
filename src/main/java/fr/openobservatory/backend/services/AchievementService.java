package fr.openobservatory.backend.services;

import fr.openobservatory.backend.achievements.*;
import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.entities.UserAchievementEntity;
import fr.openobservatory.backend.repositories.*;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AchievementService {

  private final UserAchievementRepository userAchievementRepository;

  private final Set<Achievements> achievements =
      Set.of(
          new ObserverAchievement(),
          new JudgeAchievement(),
          new FamousAchievement(),
          new HubbleAchievement(),
          new JamesWebbAchievement());

  public void checkForAchievements(ObservationEntity observation) {
    checkObservationAchievements(observation);
  }

  public void checkForAchievements(ObservationVoteEntity vote) {
    checkVoteAchievements(vote);
  }

  private void checkObservationAchievements(ObservationEntity observation) {
    var request =
        achievements.stream().map(a -> a.onObservationUpdate(observation)).filter(Objects::nonNull);
    request.forEach(
        a -> {
          if (a.level() != null) {
            UserAchievementEntity achievement;
            achievement =
                userAchievementRepository
                    .findByUserAndAchievement(observation.getAuthor(), a.achievement())
                    .orElse(
                        UserAchievementEntity.builder()
                            .achievement(a.achievement())
                            .user(a.user())
                            .build());
            achievement.setLevel(a.level());
            userAchievementRepository.save(achievement);
          }
        });
  }

  private void checkVoteAchievements(ObservationVoteEntity vote) {
    var request = achievements.stream().map(a -> a.onVoteSubmit(vote)).filter(Objects::nonNull);
    request.forEach(
        a -> {
          if (a.level() != null) {
            UserAchievementEntity achievement;
            achievement =
                userAchievementRepository
                    .findByUserAndAchievement(a.user(), a.achievement())
                    .orElse(
                        UserAchievementEntity.builder()
                            .achievement(a.achievement())
                            .user(a.user())
                            .build());
            achievement.setLevel(a.level());
            userAchievementRepository.save(achievement);
          }
        });
  }
}
