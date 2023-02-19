package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.ObservationDto;
import fr.openobservatory.backend.repositories.ObservationRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ObservationService {

  private final ModelMapper modelMapper;
  private final ObservationRepository observationRepo;

  // ---

  public List<ObservationDto> search(Integer limit, Integer page) {
    return observationRepo.findAll().stream()
        .limit(limit)
        .map(o -> modelMapper.map(o, ObservationDto.class))
        .toList();
  }
}
