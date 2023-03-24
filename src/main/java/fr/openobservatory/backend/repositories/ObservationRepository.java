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
      value =
          "SELECT * FROM observation WHERE latitude >= ?1 AND latitude <= ?2 AND longitude >= ?3 AND longitude <= ?4 AND created_at + (interval '1 hour' * (SELECT validity_time FROM celestial_body WHERE id = observation.celestial_body_id)) >= NOW()",
      nativeQuery = true)
  Collection<ObservationEntity> findAllNearby(double latX, double latY, double lngX, double lngY);
}
