package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.CelestialBodyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CelestialBodyRepository extends JpaRepository<CelestialBodyEntity, Long> {

  boolean existsCelestialBodyByNameIgnoreCase(String name);
}
