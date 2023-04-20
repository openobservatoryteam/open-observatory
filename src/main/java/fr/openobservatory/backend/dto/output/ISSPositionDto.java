package fr.openobservatory.backend.dto.output;

import java.time.Instant;
import lombok.Data;

@Data
public class ISSPositionDto {

  private boolean isCurrent;
  private double latitude;
  private double longitude;
  private Instant timestamp;
}
