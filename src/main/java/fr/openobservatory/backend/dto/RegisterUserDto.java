package fr.openobservatory.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserDto {

  @NotBlank
  @Size(min = 1, max = 32)
  private String username;

  @NotBlank private String password;

  private String biography;
}
