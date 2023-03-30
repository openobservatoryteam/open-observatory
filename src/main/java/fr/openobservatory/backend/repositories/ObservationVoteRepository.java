package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.entities.UserEntity;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservationVoteRepository extends JpaRepository<ObservationVoteEntity, Long> {
  Set<ObservationVoteEntity> findAllByObservation(ObservationEntity observation);

  Set<ObservationVoteEntity> findAllByUser(UserEntity user);

  Optional<ObservationVoteEntity> findByObservationAndUser(
      ObservationEntity observation, UserEntity user);
}
