package fr.openobservatory.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Range;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class UpdateCelestialBodyDto {

  private JsonNullable<@NotBlank @Size(min = 4, max = 64) String> name = JsonNullable.undefined();

  private JsonNullable<@NotNull @Range(min = 1, max = 12) Integer> validityTime =
      JsonNullable.undefined();

  private JsonNullable<String> image = JsonNullable.undefined();
}
