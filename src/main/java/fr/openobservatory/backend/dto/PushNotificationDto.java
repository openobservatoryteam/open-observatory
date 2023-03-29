package fr.openobservatory.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PushNotificationDto {

  private String image;

  private String link;

  @NotBlank
  @Size(max = 256)
  private String message;
}