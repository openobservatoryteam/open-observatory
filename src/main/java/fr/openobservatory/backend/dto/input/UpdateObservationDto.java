package fr.openobservatory.backend.dto.input;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Builder
@Data
public class UpdateObservationDto {

  @Default
  private JsonNullable<
          @NotNull(message = "description.required") @Size(message = "description.size", max = 500)
          String>
      description = JsonNullable.undefined();
}
