package fr.openobservatory.backend.achievements;

import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.repositories.Achievements;

public class JudgeAchievement implements Achievements {

  private static final Achievement actual = Achievement.JUDGE;

  @Override
  public Request onVoteSubmit(ObservationVoteEntity vote) {
    var author = vote.getUser();
    var level = Level.getLevel(author.getVotes().size());
    return new Request(actual, level, author);
  }
}
