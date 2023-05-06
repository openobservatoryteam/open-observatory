package fr.openobservatory.backend.dto.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UpdatePasswordDto {

  @NotNull(message = "oldPassword.required")
  private String oldPassword;

  @NotNull(message = "newPassword.required")
  @Size(message = "newPassword.size", min = 8)
  private String newPassword;
}
