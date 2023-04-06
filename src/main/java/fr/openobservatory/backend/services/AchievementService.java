package fr.openobservatory.backend.services;

import fr.openobservatory.backend.achievements.FamousAchievement;
import fr.openobservatory.backend.achievements.JudgeAchievement;
import fr.openobservatory.backend.achievements.ObserverAchievement;
import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.entities.UserAchievementEntity;
import fr.openobservatory.backend.repositories.*;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@AllArgsConstructor
@Service
public class AchievementService {

  private final UserAchievementRepository userAchievementRepository;

  private final Set<Achievements> achievements = Set.of(new ObserverAchievement(), new JudgeAchievement(), new FamousAchievement());

  public void checkForAchievements(ObservationEntity observation) {
    checkObservationAchievements(observation);
  }

  public void checkForAchievements(ObservationVoteEntity vote) {
    checkVoteAchievements(vote);
  }

  private void checkObservationAchievements(ObservationEntity observation) {
    var request = achievements.stream().map(a -> a.onObservationUpdate(observation)).filter(Objects::nonNull);
    request.forEach(a -> {
      if (a.level() != null) {
        UserAchievementEntity achievement;
        achievement = userAchievementRepository.findByUserAndAchievement(observation.getAuthor(), a.achievement()).orElse(
                new UserAchievementEntity().setUser(a.user()).setAchievement(a.achievement()));
        achievement.setLevel(a.level());
        userAchievementRepository.save(achievement);
      }
    });
  }

  private void checkVoteAchievements(ObservationVoteEntity vote) {
    var request = achievements.stream().map(a -> a.onVoteSubmit(vote)).filter(Objects::nonNull);
    request.forEach(a -> {
      if (a.level() != null) {
        UserAchievementEntity achievement;
        achievement = userAchievementRepository.findByUserAndAchievement(a.user(), a.achievement()).orElse(
                new UserAchievementEntity().setUser(a.user()).setAchievement(a.achievement()));
        if (!(achievement.getAchievement().equals(Achievements.Achievement.HUBBLE) || achievement.getAchievement().equals(Achievements.Achievement.JAMES_WEB))) {
          achievement.setLevel(a.level());
          userAchievementRepository.save(achievement);
        } else if ((achievement.getAchievement().equals(Achievements.Achievement.HUBBLE) && a.level().equals(Achievements.Level.BEGINNER))
                || (achievement.getAchievement().equals(Achievements.Achievement.JAMES_WEB) && a.level().equals(Achievements.Level.EXPERT))) {
          achievement.setLevel(a.level());
          userAchievementRepository.save(achievement);
        }
      }
    });
  }
}
