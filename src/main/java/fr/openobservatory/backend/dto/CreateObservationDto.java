package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.entities.ObservationEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class CreateObservationDto {
  @NotNull private Long celestialBodyId;

  @NotBlank
  @Range(min = 1, max = 2048)
  private String description;

  @NotNull double lng;

  @NotNull double lat;

  @NotNull
  @Range(min = 0, max = 360)
  int orientation;

  ObservationEntity.Visibility visibility;
  Instant timestamp;
}
