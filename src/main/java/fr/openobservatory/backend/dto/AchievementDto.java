package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.entities.UserAchievementEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AchievementDto {
  private String title;
  private String description;
  private String image;
  private UserAchievementEntity.Level level;
}
