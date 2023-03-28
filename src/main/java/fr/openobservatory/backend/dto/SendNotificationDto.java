package fr.openobservatory.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Data
public class SendNotificationDto {

  @URL private String link;

  @NotBlank
  @Size(max = 256)
  private String message;
}
