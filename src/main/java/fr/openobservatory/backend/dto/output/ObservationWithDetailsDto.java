package fr.openobservatory.backend.dto.output;

import fr.openobservatory.backend.entities.ObservationVoteEntity.VoteType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ObservationWithDetailsDto extends ObservationDto {

  private VoteType currentVote;
  private boolean isExpired;
  private int karma;
}
