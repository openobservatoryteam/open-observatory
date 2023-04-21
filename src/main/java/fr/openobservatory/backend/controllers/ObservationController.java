package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.input.*;
import fr.openobservatory.backend.dto.output.ObservationDto;
import fr.openobservatory.backend.dto.output.ObservationWithDetailsDto;
import fr.openobservatory.backend.dto.output.SearchResultsDto;
import fr.openobservatory.backend.services.ObservationService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/observations")
public class ObservationController {

  private final ObservationService observationService;

  // ---

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SearchResultsDto<ObservationWithDetailsDto>> search(
      Authentication authentication, PaginationDto dto) {
    if (dto.getItemsPerPage() == null) dto.setItemsPerPage(10);
    if (dto.getPage() == null) dto.setPage(0);
    var observations = observationService.search(dto, authentication.getName());
    return ResponseEntity.ok(observations);
  }

  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ObservationWithDetailsDto> create(
      Authentication authentication, @RequestBody CreateObservationDto createObservationDto) {
    var observation = observationService.create(authentication.getName(), createObservationDto);
    return ResponseEntity.ok(observation);
  }

  @GetMapping("/nearby")
  public ResponseEntity<List<ObservationDto>> findAllNearby(FindNearbyObservationsDto dto) {
    var observations = observationService.findAllNearby(dto);
    return ResponseEntity.ok(observations);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ObservationWithDetailsDto> findById(
      Authentication authentication, @PathVariable Long id) {
    var issuerUsername = authentication == null ? null : authentication.getName();
    var observation = observationService.findById(id, issuerUsername);
    return ResponseEntity.ok(observation);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ObservationWithDetailsDto> update(
      Authentication authentication, @PathVariable Long id, @RequestBody UpdateObservationDto dto) {
    var observation = observationService.update(id, dto, authentication.getName());
    return ResponseEntity.ok(observation);
  }

  @PutMapping("/{id}/vote")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> submitVote(
      Authentication authentication, @PathVariable Long id, @RequestBody SubmitVoteDto vote) {
    observationService.submitVote(id, vote, authentication.getName());
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<ObservationDto> deleteObservation(@PathVariable Long id) {
    var obs = observationService.delete(id);
    return ResponseEntity.ok(obs);
  }
}
