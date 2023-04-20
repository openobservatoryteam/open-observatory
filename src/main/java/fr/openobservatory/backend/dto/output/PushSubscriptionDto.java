package fr.openobservatory.backend.dto.output;

import java.time.Instant;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class PushSubscriptionDto {

  private String endpoint;
  private String p256dh;
  private Instant createdAt;
}
