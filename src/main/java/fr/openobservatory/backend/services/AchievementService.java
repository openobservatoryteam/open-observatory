package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.AchievementDto;
import fr.openobservatory.backend.entities.UserAchievementEntity;
import fr.openobservatory.backend.exceptions.UnknownAchievementException;
import fr.openobservatory.backend.exceptions.UnknownUserException;
import fr.openobservatory.backend.repositories.AchievementRepository;
import fr.openobservatory.backend.repositories.UserAchievementRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class AchievementService {

  private AchievementRepository achievementRepository;
  private UserAchievementRepository userAchievementRepository;
  private UserRepository userRepository;
  private ModelMapper modelMapper;

  public void checkForAchievement(String achievementTitle, String username, Integer level) {
    var achievement =
        achievementRepository
            .findAllByTitle(achievementTitle)
            .orElseThrow(UnknownAchievementException::new);
    var user =
        userRepository.findByUsernameIgnoreCase(username).orElseThrow(UnknownUserException::new);
    var candidate = userAchievementRepository.findAllByUserAndAchievement(user, achievement);
    var roundLevel = checkLevel(level);
    if (roundLevel != null) {
      UserAchievementEntity userAchievement;
      if (candidate.isPresent()) {
        userAchievement = candidate.get();
        userAchievement.setLevel(UserAchievementEntity.Level.getLevel(roundLevel));
      } else {
        userAchievement = new UserAchievementEntity();
        userAchievement.setAchievement(achievement);
        userAchievement.setUser(user);
        userAchievement.setLevel(UserAchievementEntity.Level.NEW);
      }
      userAchievementRepository.save(userAchievement);
    }
  }

  public List<AchievementDto> findByUser(String username) {
    var user =
        userRepository.findByUsernameIgnoreCase(username).orElseThrow(UnknownUserException::new);
    var achievements = userAchievementRepository.findAllByUser(user);
    return achievements.stream()
        .map(
            a -> {
              var dto = modelMapper.map(a.getAchievement(), AchievementDto.class);
              dto.setLevel(a.getLevel());
              return dto;
            })
        .toList();
  }

  private Integer checkLevel(Integer level) {
    var list = List.of(1, 10, 25, 100);
    return list.stream().filter(n -> n.equals(level)).findFirst().orElse(null);
  }
}
