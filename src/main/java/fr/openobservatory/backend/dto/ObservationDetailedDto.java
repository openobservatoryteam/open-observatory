package fr.openobservatory.backend.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ObservationDetailedDto extends ObservationDto {
  private boolean hasExpired;
  private int votes;
}
