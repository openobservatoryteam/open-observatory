package fr.openobservatory.backend.dto;

import java.time.OffsetDateTime;
import lombok.Data;

@Data
public class ObservationDto {
  private Long id;
  private String description;
  private double latitude;
  private double longitude;
  private Integer orientation;
  private OffsetDateTime time;
  private UserDto author;
}
