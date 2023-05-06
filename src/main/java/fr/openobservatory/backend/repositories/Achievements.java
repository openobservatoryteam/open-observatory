package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;

public interface Achievements {
  @AllArgsConstructor
  enum Achievement {
    JUDGE,
    OBSERVER,
    FAMOUS,
    HUBBLE,
    JAMES_WEBB
  }

  default Request onObservationUpdate(ObservationEntity observation) {
    return null;
  }

  default Request onVoteSubmit(ObservationVoteEntity vote) {
    return null;
  }

  record Request(Achievement achievement, Level level, UserEntity user) {}

  @AllArgsConstructor
  @Getter
  enum Level {
    NEW(1),
    BEGINNER(10),
    INTERMEDIATE(25),
    EXPERT(100),
    NONE(0);

    private final int count;

    public String getName() {
      return name();
    }

    public static Level getLevel(Integer count) {
      if (count == 1) {
        return Level.NEW;
      } else if (count == 10) {
        return Level.BEGINNER;
      } else if (count == 25) {
        return Level.INTERMEDIATE;
      } else if (count == 100) {
        return EXPERT;
      } else {
        return null;
      }
    }
  }
}
