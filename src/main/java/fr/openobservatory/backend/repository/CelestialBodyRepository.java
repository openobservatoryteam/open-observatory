package fr.openobservatory.backend.repository;

import fr.openobservatory.backend.models.CelestialBody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CelestialBodyRepository extends JpaRepository<CelestialBody, UUID> {
    boolean existsCelestialBodyByName(String name);
}