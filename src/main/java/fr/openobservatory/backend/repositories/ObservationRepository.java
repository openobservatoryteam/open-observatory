package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.UserEntity;
import java.util.Collection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservationRepository extends JpaRepository<ObservationEntity, Long> {

  Page<ObservationEntity> findAllByAuthor(UserEntity user, Pageable pageable);

  @Query(
      "FROM ObservationEntity o WHERE (o.latitude BETWEEN :latX AND :latY) AND (o.longitude BETWEEN :lngX AND :lngY) AND (CURRENT_TIMESTAMP - o.createdAt) < MAKE_INTERVAL(0, 0, 0, 0, o.celestialBody.validityTime)")
  Collection<ObservationEntity> findAllNearby(double latX, double latY, double lngX, double lngY);
}
