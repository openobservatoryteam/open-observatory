package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.entities.ObservationVoteEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ObservationDetailedDto extends ObservationDto {
  private ObservationVoteEntity.VoteType currentVote;
  private boolean hasExpired;
  private int karma;
}
