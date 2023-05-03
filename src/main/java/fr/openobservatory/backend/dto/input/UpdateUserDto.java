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
public class UpdateUserDto {

  @Default
  private JsonNullable<@Size(message = "biography.size", max = 500) String> biography =
      JsonNullable.undefined();

  @Default
  private JsonNullable<@NotNull(message = "isPublic.required") Boolean> isPublic =
      JsonNullable.undefined();

  @Default
  private JsonNullable<@NotNull(message = "notificationEnabled.required") Boolean>
      notificationEnabled = JsonNullable.undefined();

  @Default
  private JsonNullable<
          @NotNull(message = "notificationRadius.required")
          @Range(message = "notificationRadius.range", min = 5, max = 50) Integer>
      notificationRadius = JsonNullable.undefined();

  @Default
  private JsonNullable<
          @NotNull(message = "password.required") @Size(message = "password.size", min = 8) String>
      password = JsonNullable.undefined();
}
