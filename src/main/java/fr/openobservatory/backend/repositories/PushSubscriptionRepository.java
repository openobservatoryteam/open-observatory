package fr.openobservatory.backend.repositories;

import fr.openobservatory.backend.entities.PushSubscriptionEntity;
import fr.openobservatory.backend.entities.UserEntity;
import java.util.Collection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscriptionEntity, String> {

  Collection<PushSubscriptionEntity> findAllByUser(UserEntity user);
}
