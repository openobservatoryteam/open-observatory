package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.ObservationDetailedDto;
import fr.openobservatory.backend.dto.ObservationDto;
import fr.openobservatory.backend.dto.VoteDto;
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

  private final ModelMapper modelMapper;
  private final ObservationService observationService;

  // ---

  @GetMapping("/nearby")
  public ResponseEntity<List<ObservationDto>> nearbyObservations(
      @RequestParam Double lng, @RequestParam Double lat) {
    var observations =
        observationService.findNearbyObservations(lng, lat).stream()
            .map(o -> modelMapper.map(o, ObservationDto.class))
            .toList();
    return ResponseEntity.ok(observations);
  }

  @GetMapping
  public ResponseEntity<List<ObservationDto>> observations(int limit, int page) {
    var observations =
        observationService.search(limit, page).stream()
            .map(o -> modelMapper.map(o, ObservationDto.class))
            .toList();
    return ResponseEntity.ok(observations);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ObservationDetailedDto> getObservation(@PathVariable("id") Long id) {
    var observation =
        observationService.findById(id).map(o -> modelMapper.map(o, ObservationDetailedDto.class));
    return ResponseEntity.of(observation);
  }

  @PutMapping("/{id}/vote")
  public ResponseEntity<Void> voteObservation(
      Authentication authentication, @PathVariable Long id, @RequestBody @Valid VoteDto vote) {
    observationService.voteObservation(id, authentication.getName(), vote);
    return ResponseEntity.noContent().build();
  }
}
