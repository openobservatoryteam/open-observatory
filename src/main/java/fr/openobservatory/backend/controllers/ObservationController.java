package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.input.*;
import fr.openobservatory.backend.dto.output.ObservationDto;
import fr.openobservatory.backend.dto.output.ObservationWithDetailsDto;
import fr.openobservatory.backend.dto.output.SearchResultsDto;
import fr.openobservatory.backend.services.ObservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/observations")
@Tag(name = "Observations routes", description = "All observation's related routes")
public class ObservationController {

  private final ObservationService observationService;

  // ---

  @Operation(summary = "Return all observations with page numbering")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Return a certain page of observation list"),
        @ApiResponse(
            responseCode = "400",
            description = "Observation's number per page invalid or incorrect page number"),
        @ApiResponse(responseCode = "403", description = "Current user is not administrator")
      })
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SearchResultsDto<ObservationWithDetailsDto>> search(
      Authentication authentication, PaginationDto dto) {
    if (dto.getItemsPerPage() == null) dto.setItemsPerPage(10);
    if (dto.getPage() == null) dto.setPage(0);
    var observations = observationService.search(dto, authentication.getName());
    return ResponseEntity.ok(observations);
  }

  @Operation(summary = "Create an observation")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Observation created successfully"),
        @ApiResponse(
            responseCode = "400",
            description =
                "Request body format is not \"application/json\" OR Parameter \"celestialBodyId\" is not a valid Long or an existing celestial body id, OR Parameter \"description\" is too long OR Parameter \"[lng,lat]\" are not valid coordinates OR Parameter \"orientation\" is not between 0 and 360 OR Parameter \"timestamp\" is in the future"),
      })
  @PostMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ObservationWithDetailsDto> create(
      Authentication authentication, @RequestBody CreateObservationDto createObservationDto) {
    var observation = observationService.create(authentication.getName(), createObservationDto);
    return ResponseEntity.ok(observation);
  }

  @Operation(summary = "Get all observation around a certain user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Return all observation near to the user"),
        @ApiResponse(responseCode = "400", description = "Coordinates are not valid")
      })
  @GetMapping("/nearby")
  public ResponseEntity<List<ObservationDto>> findAllNearby(FindNearbyObservationsDto dto) {
    var observations = observationService.findAllNearby(dto);
    return ResponseEntity.ok(observations);
  }

  @Operation(summary = "Get an observation")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Return observation associated to given id"),
        @ApiResponse(
            responseCode = "400",
            description = "Parameter \"celestialBodyId\" is not a valid Long"),
        @ApiResponse(responseCode = "404", description = "Observation with given id can't be find")
      })
  @GetMapping("/{id}")
  public ResponseEntity<ObservationWithDetailsDto> findById(
      Authentication authentication, @PathVariable Long id) {
    var issuerUsername = authentication == null ? null : authentication.getName();
    var observation = observationService.findById(id, issuerUsername);
    return ResponseEntity.ok(observation);
  }

  @Operation(summary = "Modify an observation")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Observation modified successfully"),
        @ApiResponse(
            responseCode = "400",
            description =
                "Request body format is not \"application/json\" OR Parameter \"description\" is too long"),
        @ApiResponse(
            responseCode = "403",
            description = "Current user is not observation author or administrator"),
        @ApiResponse(responseCode = "404", description = "Observation with given id can't be find")
      })
  @PatchMapping("/{id}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<ObservationWithDetailsDto> update(
      Authentication authentication, @PathVariable Long id, @RequestBody UpdateObservationDto dto) {
    var observation = observationService.update(id, dto, authentication.getName());
    return ResponseEntity.ok(observation);
  }

  @Operation(summary = "Vote on observation")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Vote successfully submitted"),
        @ApiResponse(responseCode = "400", description = "Parameter \"vote\" format is invalid"),
        @ApiResponse(responseCode = "404", description = "Observation with given id can't be find")
      })
  @PutMapping("/{id}/vote")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> submitVote(
      Authentication authentication, @PathVariable Long id, @RequestBody SubmitVoteDto vote) {
    observationService.submitVote(id, vote, authentication.getName());
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Delete an observation")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Observation deleted successfully"),
        @ApiResponse(
            responseCode = "403",
            description = "Current user is not observation author or administrator"),
        @ApiResponse(responseCode = "404", description = "Observation with given id can't be find")
      })
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<ObservationDto> deleteObservation(@PathVariable Long id) {
    var obs = observationService.delete(id);
    return ResponseEntity.ok(obs);
  }
}
