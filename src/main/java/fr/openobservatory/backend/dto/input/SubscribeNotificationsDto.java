package fr.openobservatory.backend.dto.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.URL;

@Builder
@Data
public class SubscribeNotificationsDto {

  @NotNull(message = "auth.required")
  @Size(message = "auth.size", min = 24, max = 24)
  private String auth;

  @NotNull(message = "endpoint.required")
  @Size(message = "endpoint.size", max = 240)
  @URL(message = "endpoint.url")
  private String endpoint;

  @NotNull(message = "p256dh.required")
  @Size(message = "p256dh.size", min = 88, max = 88)
  private String p256dh;
}
