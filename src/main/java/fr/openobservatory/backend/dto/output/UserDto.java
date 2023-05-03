package fr.openobservatory.backend.dto.output;

import fr.openobservatory.backend.entities.UserEntity.Type;
import lombok.Data;

@Data
public class UserDto {

  private String username;
  private String avatar;
  private boolean isPublic;
  private Type type;
}
