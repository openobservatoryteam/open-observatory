package fr.openobservatory.backend.achievements;

import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.repositories.Achievements;

public class FamousAchievement implements Achievements {

    private final Achievement actual = Achievement.FAMOUS;

    @Override
    public Request onVoteSubmit(ObservationVoteEntity vote) {
        var karma = vote.getObservation().getVotes().stream().map(v -> v.getVote().getWeight()).reduce(0, Integer::sum);
        var author = vote.getUser();
        return new Request(actual, Level.getLevel(karma), author);
    }
}
