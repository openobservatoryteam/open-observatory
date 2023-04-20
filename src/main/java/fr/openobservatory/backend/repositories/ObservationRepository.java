package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.UserEntity;
import java.util.Collection;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservationRepository extends JpaRepository<ObservationEntity, Long> {

  Page<ObservationEntity> findAllByOrderByTimestampDesc(Pageable pageable);

  Page<ObservationEntity> findAllByAuthor(UserEntity user, Pageable pageable);

  Set<ObservationEntity> findAllByAuthor(UserEntity user);

  @Query(
      "FROM ObservationEntity o WHERE (o.latitude BETWEEN :latX AND :latY) AND (o.longitude BETWEEN :lngX AND :lngY) AND (CURRENT_TIMESTAMP - o.timestamp) < MAKE_INTERVAL(0, 0, 0, 0, o.celestialBody.validityTime)")
  Collection<ObservationEntity> findAllNearby(double latX, double latY, double lngX, double lngY);
}
