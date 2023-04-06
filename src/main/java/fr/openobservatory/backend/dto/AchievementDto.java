package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.entities.UserAchievementEntity;
import fr.openobservatory.backend.repositories.Achievements;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AchievementDto {
  private Achievements.Achievement achievement;
  private Achievements.Level level;
}
