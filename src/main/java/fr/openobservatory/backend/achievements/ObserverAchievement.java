package fr.openobservatory.backend.achievements;

import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.repositories.Achievements;

public class ObserverAchievement implements Achievements {

  private static final Achievement actual = Achievement.OBSERVER;

  @Override
  public Request onObservationUpdate(ObservationEntity observation) {
    var author = observation.getAuthor();
    var level = Level.getLevel(author.getObservations().size());
    return new Request(actual, level, author);
  }
}
