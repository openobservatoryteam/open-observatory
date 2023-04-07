package fr.openobservatory.backend.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Data
public class PushSubscriptionKeyDto {

  private String key;
}
