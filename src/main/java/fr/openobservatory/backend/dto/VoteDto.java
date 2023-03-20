package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.entities.ObservationVoteEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class VoteDto {

  ObservationVoteEntity.VoteType vote;
}
