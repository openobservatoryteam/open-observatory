package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.repositories.Achievements;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AchievementDto {
  private Achievements.Achievement achievement;
  private LevelDto level;

  @Data
  private static class LevelDto {
    private String name;
    private int count;
  }
}
