package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.CelestialBodyDto;
import fr.openobservatory.backend.exceptions.ConflictException;
import fr.openobservatory.backend.models.SearchResults;
import fr.openobservatory.backend.services.CelestialBodyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/celestial-bodies")
public class CelestialBodyController {

    private final CelestialBodyService celestialBodyService;

    @Autowired
    public CelestialBodyController(CelestialBodyService celestialBodyService) {
        this.celestialBodyService = celestialBodyService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<SearchResults<CelestialBodyDto>> getCelestialBodies(
            @RequestParam(required = false, defaultValue = "10") Integer limit,
            @RequestParam(required = false, defaultValue = "0") Integer page) {

        if (limit < 1 || limit > 100) {
            return ResponseEntity.badRequest().build();
        }
        if (page < 0) {
            return ResponseEntity.badRequest().build();
        }
        Pageable pageable = PageRequest.of(page, limit);
        SearchResults<CelestialBodyDto> results = celestialBodyService.getCelestialBodies(pageable);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CelestialBodyDto> getCelestialBodyById(@PathVariable UUID id) {
        Optional<CelestialBodyDto> celestialBody = celestialBodyService.getCelestialBodyById(id);
        if (!celestialBody.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(celestialBody.get(), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<CelestialBodyDto> createCelestialBody(@RequestBody CelestialBodyDto celestialBodyDto) {
        // TODO 400 Bad Request
        CelestialBodyDto createdCelestialBody;
        try {
            createdCelestialBody = celestialBodyService.addCelestialBody(celestialBodyDto);
        } catch (ConflictException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        return new ResponseEntity<>(createdCelestialBody, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<CelestialBodyDto> updateCelestialBody(@RequestBody CelestialBodyDto celestialBodyDto) {
        // TODO
        CelestialBodyDto updatedCelestialBody = celestialBodyService.updateCelestialBody(celestialBodyDto);
        return new ResponseEntity<>(updatedCelestialBody, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<Void> deleteCelestialBody(@PathVariable UUID id) {
        // TODO
        celestialBodyService.deleteCelestialBody(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
