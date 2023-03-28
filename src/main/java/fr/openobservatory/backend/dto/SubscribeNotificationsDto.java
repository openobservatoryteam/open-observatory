package fr.openobservatory.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubscribeNotificationsDto {

  @NotNull private String auth;
  @NotNull private String endpoint;
  @NotNull private String p256dh;
}
