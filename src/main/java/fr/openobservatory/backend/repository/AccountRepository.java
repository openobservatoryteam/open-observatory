package fr.openobservatory.backend.repository;

import fr.openobservatory.backend.models.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account, Long> {

    Optional<Account> findById(Long id);

    Optional<Account> findByUsername(String username);
}
