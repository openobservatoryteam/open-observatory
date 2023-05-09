package fr.openobservatory.backend.dto.input;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openapitools.jackson.nullable.JsonNullable;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UpdateObservationDto {

  @Default
  private JsonNullable<@Size(message = "description.size", max = 500) String> description =
      JsonNullable.undefined();
}
