package fr.openobservatory.backend.dto.input;

import fr.openobservatory.backend.entities.ObservationVoteEntity.VoteType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubmitVoteDto {

  VoteType vote;
}
