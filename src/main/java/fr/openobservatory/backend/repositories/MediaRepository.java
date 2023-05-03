package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.MediaEntity;
import org.springframework.data.repository.CrudRepository;

public interface MediaRepository extends CrudRepository<MediaEntity, Long> {
}
