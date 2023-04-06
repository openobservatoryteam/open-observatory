package fr.openobservatory.backend.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.openapitools.jackson.nullable.JsonNullable;

@AllArgsConstructor
@Data
public class UpdateProfileDto {

  private JsonNullable<@Size(max = 2048) String> biography;
  private JsonNullable<String> avatar;
  private JsonNullable<Boolean> isPublic;
  private JsonNullable<Integer> radius;
  private JsonNullable<Boolean> notificationsEnabled;
}
