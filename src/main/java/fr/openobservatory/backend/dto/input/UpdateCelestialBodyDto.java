package fr.openobservatory.backend.dto.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.openapitools.jackson.nullable.JsonNullable;

@Builder
@Data
public class UpdateCelestialBodyDto {

  @Default
  private JsonNullable<
          @NotNull(message = "name.required") @Size(message = "name.size", min = 4, max = 64)
          String>
      name = JsonNullable.undefined();

  @Default
  private JsonNullable<
          @NotNull(message = "validityTime.required")
          @Range(message = "validityTime.range", min = 1, max = 12) Integer>
      validityTime = JsonNullable.undefined();

  @Default private JsonNullable<String> image = JsonNullable.undefined();
}
