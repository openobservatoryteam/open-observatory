package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.entities.ObservationEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Data
public class CreateObservationDto {

  @NotNull private Long celestialBodyId;

  @Size(max = 2048)
  private String description;

  @NotNull private Double lng;

  @NotNull private Double lat;

  @NotNull
  @Range(min = 0, max = 360)
  private Integer orientation;

  @NotNull private ObservationEntity.Visibility visibility;

  @NotNull private OffsetDateTime timestamp;
}
