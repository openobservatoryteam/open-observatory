package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.entities.ObservationEntity;
import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class ObservationDto {
  private Long id;
  private String description;
  private double latitude;
  private double longitude;
  private Integer orientation;
  private CelestialBodyDto celestialBody;
  private UserDto author;
  private OffsetDateTime time;
  private ObservationEntity.Visibility visibility;
}
