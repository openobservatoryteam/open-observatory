package fr.openobservatory.backend.dto.input;

import fr.openobservatory.backend.entities.UserEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateUserDto {

  @NotNull(message = "username.required")
  @Pattern(message = "username.format", regexp = UserEntity.USERNAME_PATTERN)
  private String username;

  @NotNull(message = "password.required")
  @Size(message = "password.size", min = 8)
  private String password;

  @Size(message = "biography.size", max = 500)
  private String biography;
}
