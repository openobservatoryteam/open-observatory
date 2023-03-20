package fr.openobservatory.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@Data
public class UpdateObservationDto {

  private JsonNullable<@NotBlank @Size(max = 2000) String> description;
}
