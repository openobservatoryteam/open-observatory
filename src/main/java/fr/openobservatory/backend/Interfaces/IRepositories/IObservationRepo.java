package fr.openobservatory.backend.Interfaces.IRepositories;

import fr.openobservatory.backend.Implementations.Entities.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IObservationRepo extends JpaRepository<Observation,Integer>
{

}
