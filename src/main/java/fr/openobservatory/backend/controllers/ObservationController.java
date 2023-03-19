package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.CreateObservationDto;
import fr.openobservatory.backend.dto.ObservationDetailedDto;
import fr.openobservatory.backend.dto.ObservationDto;
import fr.openobservatory.backend.services.ObservationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/observations")
public class ObservationController {

  private final ObservationService observationService;
  private final ModelMapper modelMapper;

  // ---

  @GetMapping("/nearby")
  public ResponseEntity<List<ObservationDto>> nearbyObservations(
      @RequestParam Double lng, @RequestParam Double lat) {
    return ResponseEntity.ok(observationService.findNearbyObservations(lng, lat));
  }

  @PostMapping
  public ResponseEntity<ObservationDto> createObservation(
      Authentication authentication, @Valid CreateObservationDto createObservationDto) {
    return ResponseEntity.ok(
        observationService.createObservation(authentication.getName(), createObservationDto));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ObservationDto> update(
      @PathVariable Long id, @RequestBody String description) {
    return ResponseEntity.ok(observationService.update(id, description));
  }

  @GetMapping
  public ResponseEntity<List<ObservationDto>> observations(Integer limit, Integer page) {
    return ResponseEntity.ok(observationService.search(limit, page));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ObservationDetailedDto> getObservation(@PathVariable("id") Long id) {
    var observation = observationService.findById(id);
    if (observation == null) {
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(observation);
  }
}
