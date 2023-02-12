package fr.openobservatory.backend.Implementations.Services;

import fr.openobservatory.backend.Implementations.Dtos.ObservationDto;
import fr.openobservatory.backend.Implementations.Entities.Observation;
import fr.openobservatory.backend.Interfaces.IRepositories.IObservationRepo;
import fr.openobservatory.backend.Interfaces.IServices.IObservationService;
import fr.openobservatory.backend.Utilities.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ObservationService implements IObservationService
{
    @Autowired
    private IObservationRepo observationRepo;
    @Override
    public List<ObservationDto> searchObservations(Integer limit, Integer page)
    {
        //get observations
        List<Observation> observations = observationRepo.findAll().stream().limit(limit).collect(Collectors.toList());

        //create dto list
        List<ObservationDto> dtos = new ArrayList<>();

        //map observations to dto
        observations.forEach(x -> dtos.add(Mappers.ObservationToDto(x)));

        return dtos;
    }
}
