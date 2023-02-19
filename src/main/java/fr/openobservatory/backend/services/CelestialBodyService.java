package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.CelestialBodyDto;
import fr.openobservatory.backend.exceptions.ConflictException;
import fr.openobservatory.backend.models.CelestialBody;
import fr.openobservatory.backend.models.SearchResults;
import fr.openobservatory.backend.repository.CelestialBodyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CelestialBodyService {
    private final CelestialBodyRepository celestialBodyRepository;
    private final ModelMapper modelMapper;

    public CelestialBodyService(CelestialBodyRepository celestialBodyRepository) {
        this.celestialBodyRepository = celestialBodyRepository;
        this.modelMapper = new ModelMapper();
    }

    public SearchResults<CelestialBodyDto> getCelestialBodies(Pageable pageable) {
        Page<CelestialBody> celestialBodies = celestialBodyRepository.findAll(pageable);

        List<CelestialBodyDto> celestialBodyDtos = celestialBodies.stream()
                .map(celestialBody -> modelMapper.map(celestialBody, CelestialBodyDto.class))
                .collect(Collectors.toList());

        return new SearchResults<>(celestialBodyDtos,
                                    celestialBodies.getNumberOfElements(),
                                    (int) celestialBodies.getTotalElements(),
                                    celestialBodies.getNumber(),
                                    celestialBodies.getTotalPages());
    }

    public Optional<CelestialBodyDto> getCelestialBodyById(UUID id) {
        Optional<CelestialBody> celestialBody = celestialBodyRepository.findById(id);
        return Optional.ofNullable(modelMapper.map(celestialBody, CelestialBodyDto.class));
    }

    public CelestialBodyDto addCelestialBody(CelestialBodyDto celestialBodyDto) {
        if (celestialBodyRepository.existsCelestialBodyByName(celestialBodyDto.getName())) {
            throw new ConflictException("Celestial body's name already in use.");
        }

        CelestialBody celestialBody = new CelestialBody();
        celestialBody.setName(celestialBodyDto.getName());
        celestialBody.setImage(celestialBodyDto.getImage());
        celestialBody.setValidityTime(celestialBodyDto.getValidityTime());

        celestialBody = celestialBodyRepository.save(celestialBody);

        return modelMapper.map(celestialBody, celestialBodyDto.getClass());
    }

    public CelestialBodyDto updateCelestialBody(CelestialBodyDto celestialBodyDto, UUID id) {
        CelestialBody celestialBody = celestialBodyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (celestialBodyDto.getImage() != null) {
            if (celestialBodyRepository.existsCelestialBodyByName(celestialBodyDto.getName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT);
            }
            celestialBody.setName(celestialBodyDto.getName());
        }

        if (celestialBodyDto.getImage() != null)
            celestialBody.setImage(celestialBodyDto.getImage());

        if (celestialBodyDto.getValidityTime() != null)
            celestialBody.setValidityTime(celestialBodyDto.getValidityTime());

        return modelMapper.map(celestialBodyRepository.save(celestialBody), CelestialBodyDto.class);
    }

    public void deleteCelestialBody(UUID id) {
        // TODO
        celestialBodyRepository.deleteById(id);
    }
}