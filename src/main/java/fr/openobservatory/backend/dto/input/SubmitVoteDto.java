package fr.openobservatory.backend.dto.input;

import fr.openobservatory.backend.entities.ObservationVoteEntity.VoteType;
import fr.openobservatory.backend.validation.EnumValue;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubmitVoteDto {

  @EnumValue(message = "vote.invalid", value = VoteType.class)
  private String vote;
}
