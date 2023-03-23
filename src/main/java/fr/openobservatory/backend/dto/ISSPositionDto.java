package fr.openobservatory.backend.dto;

import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class ISSPositionDto {

  private boolean isCurrent;
  private double latitude;
  private double longitude;
  private OffsetDateTime timestamp;
}
