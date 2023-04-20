package fr.openobservatory.backend.dto.output;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SelfUserDto extends UserWithProfileDto {

  private boolean notificationsEnabled;
  private Integer notificationRadius;
}
