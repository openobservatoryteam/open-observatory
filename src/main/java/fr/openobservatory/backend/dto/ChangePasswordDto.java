package fr.openobservatory.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class ChangePasswordDto {
  private String oldPassword;
  private String newPassword;
}
