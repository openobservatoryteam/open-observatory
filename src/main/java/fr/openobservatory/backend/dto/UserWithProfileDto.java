package fr.openobservatory.backend.dto;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserWithProfileDto extends UserDto {

  private List<AchievementDto> achievements;
  private String biography;
  private Integer karma;
}
