package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.ObservationDetailedDto;
import fr.openobservatory.backend.dto.ObservationDto;
import fr.openobservatory.backend.services.ObservationService;
import java.util.List;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
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

  @GetMapping
  public ResponseEntity<List<ObservationDto>> observations(int limit, int page) {
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
