package fr.openobservatory.backend.achievements;

import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.repositories.Achievements;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class FamousAchievementTest {

    @DisplayName("FamousAchievementTest#onVoteSubmit should return request with corresponding level")
    @Test
    public void onVoteSubmit_should_return_request_with_good_argument() {
        //Given
        var author = new UserEntity();
        var observation = new ObservationEntity();
        var observationVote = new ObservationVoteEntity();
        observationVote.setVote(ObservationVoteEntity.VoteType.UPVOTE);
        observation.setVotes(Set.of(observationVote));
        author.setObservations(Set.of(observation));
        var vote = new ObservationVoteEntity();
        vote.setUser(author);
        var achievement = new FamousAchievement();
        //When
        var request = achievement.onVoteSubmit(vote);
        //Then
        assertThat(request.achievement()).isEqualTo(Achievements.Achievement.FAMOUS);
        assertThat(request.level()).isEqualTo(Achievements.Level.NEW);
        assertThat(request.user()).isEqualTo(author);
    }

}