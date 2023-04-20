package fr.openobservatory.backend.dto.output;

import fr.openobservatory.backend.entities.ObservationEntity.Visibility;
import java.time.Instant;
import lombok.Data;

@Data
public class ObservationDto {

  private Long id;
  private UserDto author;
  private CelestialBodyDto celestialBody;
  private String description;
  private double latitude;
  private double longitude;
  private int orientation;
  private Visibility visibility;
  private Instant timestamp;
}
