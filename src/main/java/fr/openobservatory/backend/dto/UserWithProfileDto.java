package fr.openobservatory.backend.dto;

import java.util.Set;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserWithProfileDto extends UserDto {

  private Set<AchievementDto> achievements;
  private String biography;
  private Integer karma;
}
