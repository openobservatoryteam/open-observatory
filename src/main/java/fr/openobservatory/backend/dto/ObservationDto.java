package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.entities.ObservationEntity;
import java.time.OffsetDateTime;
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
  private ObservationEntity.Visibility visibility;
  private OffsetDateTime createdAt;
}
