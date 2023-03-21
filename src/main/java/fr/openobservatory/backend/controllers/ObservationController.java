package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.*;
import fr.openobservatory.backend.services.ObservationService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/observations")
public class ObservationController {

  private final ModelMapper modelMapper;
  private final ObservationService observationService;

  // ---

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ObservationDto> create(
      Authentication authentication,
      @RequestBody @Valid CreateObservationDto createObservationDto) {
    var observation =
        observationService.createObservation(authentication.getName(), createObservationDto);
    return ResponseEntity.ok(modelMapper.map(observation, ObservationDto.class));
  }

  @GetMapping
  public ResponseEntity<List<ObservationDto>> findAll(
      @RequestParam Integer limit, @RequestParam Integer page) {
    var observations =
        observationService.search(limit, page).stream()
            .map(o -> modelMapper.map(o, ObservationDto.class))
            .toList();
    return ResponseEntity.ok(observations);
  }

  @GetMapping("/nearby")
  public ResponseEntity<List<ObservationDto>> findAllNearby(
      @RequestParam Double lng, @RequestParam Double lat) {
    var observations =
        observationService.findNearbyObservations(lng, lat).stream()
            .map(o -> modelMapper.map(o, ObservationDto.class))
            .toList();
    return ResponseEntity.ok(observations);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ObservationDetailedDto> findOneById(@PathVariable Long id) {
    var observation =
        observationService.findById(id).map(o -> modelMapper.map(o, ObservationDetailedDto.class));
    return ResponseEntity.of(observation);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ObservationDto> update(
      Authentication authentication,
      @PathVariable Long id,
      @RequestBody @Valid UpdateObservationDto dto) {
    var observation = observationService.update(id, dto, authentication.getName());
    return ResponseEntity.ok(modelMapper.map(observation, ObservationDto.class));
  }

  @PutMapping("/{id}/vote")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> vote(
      Authentication authentication, @PathVariable Long id, @RequestBody @Valid VoteDto vote) {
    observationService.voteObservation(id, authentication.getName(), vote);
    return ResponseEntity.noContent().build();
  }
}
