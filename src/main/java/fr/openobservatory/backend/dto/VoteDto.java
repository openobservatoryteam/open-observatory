package fr.openobservatory.backend.dto;

import fr.openobservatory.backend.entities.ObservationVoteEntity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class VoteDto {

  @Enumerated(EnumType.STRING)
  ObservationVoteEntity.VoteType vote;
}
