package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.CelestialBodyDto;
import fr.openobservatory.backend.repository.CelestialBodyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CelestialBodyService {
    private final CelestialBodyRepository celestialBodyRepository;

    public CelestialBodyService(CelestialBodyRepository celestialBodyRepository) {
        this.celestialBodyRepository = celestialBodyRepository;
    }

    public List<CelestialBodyDto> getAllCelestialBodies() {
        return celestialBodyRepository.findAll();
    }

    public Optional<CelestialBodyDto> getCelestialBodyById(UUID id) {
        return celestialBodyRepository.findById(id);
    }

    public CelestialBodyDto addCelestialBody(CelestialBodyDto celestialBodyDto) {
        return celestialBodyRepository.save(celestialBodyDto);
    }

    public CelestialBodyDto updateCelestialBody(CelestialBodyDto celestialBodyDto) {
        return celestialBodyRepository.save(celestialBodyDto);
    }

    public void deleteCelestialBody(UUID id) {
        celestialBodyRepository.deleteById(id);
    }
}