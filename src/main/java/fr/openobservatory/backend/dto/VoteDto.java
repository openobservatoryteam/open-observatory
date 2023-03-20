package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.entities.ObservationVoteEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VoteDto {

  ObservationVoteEntity.VoteType vote;
}
