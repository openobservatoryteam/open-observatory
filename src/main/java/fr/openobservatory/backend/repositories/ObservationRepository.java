package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ObservationRepository extends JpaRepository<ObservationEntity, Long> {

  Page<ObservationEntity> findAllByAuthor(UserEntity user, Pageable pageable);
}
