package fr.openobservatory.backend.dto.input;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Builder
@Data
public class FindNearbyObservationsDto {

  @NotNull(message = "latitude.required")
  @Range(message = "latitiude.range", min = -90, max = 90)
  private Double latitude;

  @NotNull(message = "longitude.required")
  @Range(message = "longitude.range", min = -180, max = 180)
  private Double longitude;

  @NotNull(message = "radius.required")
  @Range(message = "radius.range", min = 0, max = 250)
  private Double radius;
}
