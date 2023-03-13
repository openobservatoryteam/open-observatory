package fr.openobservatory.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserWithProfileDto extends UserDto {
  private List<AchievementDto> achievements;
  private String biography;
  private Integer karma;
}
