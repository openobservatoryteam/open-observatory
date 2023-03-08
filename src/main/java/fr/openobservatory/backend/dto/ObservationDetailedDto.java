package fr.openobservatory.backend.dto;

import lombok.Data;

@Data
public class ObservationDetailedDto extends ObservationDto {
  private boolean hasExpired;
  private int votes;
}
