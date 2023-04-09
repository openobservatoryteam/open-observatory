package fr.openobservatory.backend.achievements;

import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.repositories.Achievements;

public class JamesWebbAchievement implements Achievements {

  private static final Achievement actual = Achievement.JAMES_WEB;

  @Override
  public Request onVoteSubmit(ObservationVoteEntity vote) {
    var karma =
        vote.getObservation().getVotes().stream()
            .map(v -> v.getVote().getWeight())
            .reduce(0, Integer::sum);
    if (karma >= 100) {
      return new Request(actual, Level.NONE, vote.getObservation().getAuthor());
    }
    return new Request(actual, null, vote.getObservation().getAuthor());
  }
}
