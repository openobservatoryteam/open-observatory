package fr.openobservatory.backend.Interfaces.IServices;

import fr.openobservatory.backend.Implementations.Dtos.ObservationDto;

import java.util.List;

public interface IObservationService
{
    /*
     * Finds and returns observations for the given parameters
     */
    List<ObservationDto> searchObservations(Integer limit, Integer page);
}
