package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.CelestialBodyDto;
import fr.openobservatory.backend.dto.CreateCelestialBodyDto;
import fr.openobservatory.backend.dto.SearchDto;
import fr.openobservatory.backend.dto.SearchResultsDto;
import fr.openobservatory.backend.dto.UpdateCelestialBodyDto;
import fr.openobservatory.backend.entities.CelestialBodyEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CelestialBodyService {

  private final CelestialBodyRepository celestialBodyRepository;
  private final ModelMapper modelMapper;
  private final Validator validator;

  // ---

  public CelestialBodyDto create(CreateCelestialBodyDto dto) {
    var violations = validator.validate(dto);
    if (!violations.isEmpty())
      throw new ValidationException(violations);
    if (celestialBodyRepository.existsCelestialBodyByNameIgnoreCase(dto.getName()))
      throw new CelestialBodyNameAlreadyUsedException();
    var celestialBody = modelMapper.map(dto, CelestialBodyEntity.class);
    return modelMapper.map(celestialBodyRepository.save(celestialBody), CelestialBodyDto.class);
  }

  public void delete(Long id) {
    if (!celestialBodyRepository.existsById(id))
      throw new UnknownCelestialBodyException();
    celestialBodyRepository.deleteById(id);
  }

  public CelestialBodyDto findById(Long id) {
    return celestialBodyRepository
        .findById(id)
        .map(o -> modelMapper.map(o, CelestialBodyDto.class))
        .orElseThrow(UnknownCelestialBodyException::new);
  }

  public SearchResultsDto<CelestialBodyDto> search(SearchDto dto) {
    var violations = validator.validate(dto);
    if (!violations.isEmpty())
      throw new ValidationException(violations);
    var pageable = PageRequest.of(dto.getPage(), dto.getItemsPerPage());
    return SearchResultsDto.from(
        celestialBodyRepository
            .findAll(pageable)
            .map(o -> modelMapper.map(o, CelestialBodyDto.class)));
  }

  public CelestialBodyDto update(Long id, UpdateCelestialBodyDto dto) {
    var violations = validator.validate(dto);
    if (!violations.isEmpty())
      throw new ValidationException(violations);
    var celestialBody =
        celestialBodyRepository.findById(id).orElseThrow(UnknownCelestialBodyException::new);
    if (dto.getName().isPresent()) {
      var name = dto.getName().get();
      if (!celestialBody.getName().equalsIgnoreCase(name)
          && celestialBodyRepository.existsCelestialBodyByNameIgnoreCase(name))
        throw new CelestialBodyNameAlreadyUsedException();
      celestialBody.setName(name);
    }
    if (dto.getValidityTime().isPresent())
      celestialBody.setValidityTime(dto.getValidityTime().get());
    if (dto.getImage().isPresent())
      celestialBody.setImage(dto.getImage().get());
    return modelMapper.map(celestialBodyRepository.save(celestialBody), CelestialBodyDto.class);
  }
}
