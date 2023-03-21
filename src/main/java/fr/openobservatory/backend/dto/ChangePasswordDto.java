package fr.openobservatory.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class ChangePasswordDto {

  private String oldPassword;
  private String newPassword;
}
