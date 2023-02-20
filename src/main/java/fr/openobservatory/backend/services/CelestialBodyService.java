package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.CelestialBodyDto;
import fr.openobservatory.backend.dto.CreateCelestialBodyDto;
import fr.openobservatory.backend.dto.SearchResultsDto;
import fr.openobservatory.backend.dto.UpdateCelestialBodyDto;
import fr.openobservatory.backend.entities.CelestialBodyEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CelestialBodyService {

  private final CelestialBodyRepository celestialBodyRepository;
  private final ModelMapper modelMapper;

  // ---

  public CelestialBodyDto create(CreateCelestialBodyDto dto) {
    if (dto.getName().length() < 4 || dto.getName().length() > 64)
      throw new InvalidCelestialBodyNameException();
    if (dto.getValidityTime() < 1 || dto.getValidityTime() > 12)
      throw new InvalidCelestialBodyValidityTimeException();
    if (celestialBodyRepository.existsCelestialBodyByNameIgnoreCase(dto.getName()))
      throw new CelestialBodyNameAlreadyUsedException();
    var celestialBody = new CelestialBodyEntity();
    celestialBody.setName(dto.getName());
    celestialBody.setValidityTime(dto.getValidityTime());
    celestialBody.setImage(dto.getImage());
    return modelMapper.map(celestialBodyRepository.save(celestialBody), CelestialBodyDto.class);
  }

  public void delete(Long id) {
    if (!celestialBodyRepository.existsById(id)) throw new UnknownCelestialBodyException();
    celestialBodyRepository.deleteById(id);
  }

  public Optional<CelestialBodyDto> findById(Long id) {
    return celestialBodyRepository
        .findById(id)
        .map(c -> modelMapper.map(c, CelestialBodyDto.class));
  }

  public SearchResultsDto<CelestialBodyDto> search(Integer page, Integer itemsPerPage) {
    if (itemsPerPage < 0 || itemsPerPage > 10 || page < 0) throw new InvalidPaginationException();
    var pageable = PageRequest.of(page, itemsPerPage);
    var celestialBodies = celestialBodyRepository.findAll(pageable);
    var celestialBodiesDto =
        celestialBodies.stream()
            .map(celestialBody -> modelMapper.map(celestialBody, CelestialBodyDto.class))
            .toList();
    return new SearchResultsDto<>(
        celestialBodiesDto,
        celestialBodies.getNumberOfElements(),
        (int) celestialBodies.getTotalElements(),
        celestialBodies.getNumber(),
        celestialBodies.getTotalPages());
  }

  public CelestialBodyDto update(Long id, UpdateCelestialBodyDto dto) {
    var celestialBody =
        celestialBodyRepository.findById(id).orElseThrow(UnknownCelestialBodyException::new);
    if (dto.getName().isPresent()) {
      var name = dto.getName().get();
      if (name.length() < 4 || name.length() > 64) throw new InvalidCelestialBodyNameException();
      if (!celestialBody.getName().equalsIgnoreCase(name)
              && celestialBodyRepository.existsCelestialBodyByNameIgnoreCase(name))
        throw new CelestialBodyNameAlreadyUsedException();
      celestialBody.setName(name);
    }
    if (dto.getValidityTime().isPresent()) {
      var validityTime = dto.getValidityTime().get();
      if (validityTime < 1 || validityTime > 12)
        throw new InvalidCelestialBodyValidityTimeException();
      celestialBody.setValidityTime(validityTime);
    }
    if (dto.getImage().isPresent()) {
      var image = dto.getImage().get();
      celestialBody.setImage(image);
    }
    return modelMapper.map(celestialBodyRepository.save(celestialBody), CelestialBodyDto.class);
  }
}
