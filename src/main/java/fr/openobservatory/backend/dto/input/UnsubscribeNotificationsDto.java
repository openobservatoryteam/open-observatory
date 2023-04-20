package fr.openobservatory.backend.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UnsubscribeNotificationsDto {

  @NotNull(message = "endpoint.required")
  private String endpoint;
}
