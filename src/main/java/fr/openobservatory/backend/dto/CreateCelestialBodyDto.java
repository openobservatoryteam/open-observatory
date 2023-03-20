package fr.openobservatory.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

@AllArgsConstructor
@Data
public class CreateCelestialBodyDto {

  @NotBlank
  @Size(min = 4, max = 64)
  private String name;

  @NotNull
  @Range(min = 1, max = 12)
  private Integer validityTime;

  private String image;
}
