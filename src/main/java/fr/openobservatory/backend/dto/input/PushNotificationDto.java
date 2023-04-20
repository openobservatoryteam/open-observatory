package fr.openobservatory.backend.dto.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PushNotificationDto {

  private String image;

  private String link;

  @NotBlank
  @Size(max = 256)
  private String code;
}
