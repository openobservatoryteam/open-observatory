package fr.openobservatory.backend.achievements;

import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.repositories.Achievements;

public class FamousAchievement implements Achievements {

  private static final Achievement actual = Achievement.FAMOUS;

  @Override
  public Request onVoteSubmit(ObservationVoteEntity vote) {
    var author = vote.getUser();
    var observations = author.getObservations();
    var karma =
        observations.stream()
            .map(
                o ->
                    o.getVotes().stream().map(v -> v.getVote().getWeight()).reduce(0, Integer::sum))
            .reduce(0, Integer::sum);
    return new Request(actual, Level.getLevel(karma), author);
  }
}
