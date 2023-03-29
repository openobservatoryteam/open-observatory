package fr.openobservatory.backend.dto;

import java.time.OffsetDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class PushSubscriptionDto {

  private String endpoint;
  private String p256dh;
  private OffsetDateTime createdAt;
}
