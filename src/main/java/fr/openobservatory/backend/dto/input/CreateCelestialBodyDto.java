package fr.openobservatory.backend.dto.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@Builder
@Data
public class CreateCelestialBodyDto {

  @NotNull(message = "name.required")
  @Size(message = "name.size", min = 4, max = 64)
  private String name;

  @NotNull(message = "validityTime.required")
  @Range(message = "validityTime.range", min = 1, max = 12)
  private Integer validityTime;

  private String image;
}
