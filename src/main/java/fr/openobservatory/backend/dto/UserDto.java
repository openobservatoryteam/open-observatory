package fr.openobservatory.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.openobservatory.backend.entities.UserEntity;
import java.time.Instant;
import lombok.Data;

@Data
public class UserDto {

  @JsonIgnore private Long id;

  private String username;

  @JsonIgnore private String password;

  private String avatar;

  private UserEntity.Type type;

  @JsonIgnore private Instant createdAt;
}
