package fr.openobservatory.backend.dto.input;

import fr.openobservatory.backend.entities.ObservationEntity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Builder
@Data
public class CreateObservationDto {

  @NotNull(message = "celestialBodyId.required")
  @Positive(message = "celestialBodyId.positive")
  private Long celestialBodyId;

  @Size(message = "description.size", max = 500)
  private String description;

  @NotNull(message = "longitude.required")
  @Range(message = "longitude.range", min = -180, max = 180)
  private Double longitude;

  @NotNull(message = "latitude.required")
  @Range(message = "latitude.range", min = -90, max = 90)
  private Double latitude;

  @NotNull(message = "orientation.required")
  @Range(message = "orientation.range", min = 0, max = 360)
  private Integer orientation;

  @NotNull(message = "visibility.required")
  private ObservationEntity.Visibility visibility;

  @NotNull(message = "timestamp.required")
  @PastOrPresent(message = "timestamp.pastOrPresent")
  private Instant timestamp;
}
