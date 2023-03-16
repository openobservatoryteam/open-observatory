package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.entities.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RegisterUserDto {

  @NotNull
  @Pattern(regexp = UserEntity.USERNAME_PATTERN)
  private String username;

  @NotBlank private String password;

  private String biography;
}
