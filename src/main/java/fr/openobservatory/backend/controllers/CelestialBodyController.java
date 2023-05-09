package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.input.CreateCelestialBodyDto;
import fr.openobservatory.backend.dto.input.PaginationDto;
import fr.openobservatory.backend.dto.input.UpdateCelestialBodyDto;
import fr.openobservatory.backend.dto.output.CelestialBodyDto;
import fr.openobservatory.backend.dto.output.SearchResultsDto;
import fr.openobservatory.backend.services.CelestialBodyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/celestial-bodies")
@Tag(name = "CelestialBodies routes", description = "All celestial body's related routes")
public class CelestialBodyController {

  private final CelestialBodyService celestialBodyService;

  // ---
  @Operation(summary = "Get all celestial body with page numbering")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Return a certain page of celestial body list"),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid celestial body number per page or incorrect page number"),
        @ApiResponse(responseCode = "403", description = "Current user is not administrator")
      })
  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SearchResultsDto<CelestialBodyDto>> search(PaginationDto dto) {
    if (dto.getItemsPerPage() == null) dto.setItemsPerPage(10);
    if (dto.getPage() == null) dto.setPage(0);
    var celestialBodies = celestialBodyService.search(dto);
    return ResponseEntity.ok(celestialBodies);
  }

  @Operation(summary = "Get a celestial body with his id")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Return celestial body associated to given id"),
        @ApiResponse(responseCode = "400", description = "Parameter \"id\" is not a valid Long"),
        @ApiResponse(
            responseCode = "404",
            description = "Celestial body with given id can't be find")
      })
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<CelestialBodyDto> findById(@PathVariable Long id) {
    var celestialBody = celestialBodyService.findById(id);
    return ResponseEntity.ok(celestialBody);
  }

  @Operation(summary = "Create a celestial body")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Celestial body create successfully"),
        @ApiResponse(
            responseCode = "400",
            description =
                "Request body format is not \"application/json\" OR Parameter \"name\" format is incorrect, OR Parameter \"image\" is not a valid image OR Parameter \"validityTime\" is not a valid duration"),
        @ApiResponse(responseCode = "403", description = "Current user is not administrator"),
        @ApiResponse(responseCode = "409", description = "Name is already used")
      })
  @PostMapping
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<CelestialBodyDto> create(@RequestBody CreateCelestialBodyDto dto) {
    var celestialBody = celestialBodyService.create(dto);
    return ResponseEntity.created(URI.create("/celestial-bodies/" + celestialBody.getId()))
        .body(celestialBody);
  }

  @Operation(summary = "Modify celestial body")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Celestial body modified successfully"),
        @ApiResponse(
            responseCode = "400",
            description =
                "Request body format is not \"application/json\" OR Parameter \"name\" format is incorrect, OR Parameter \"image\" is not a valid image OR Parameter \"validityTime\" is not a valid duration OR Parameter \"id\" is not a valid Long"),
        @ApiResponse(responseCode = "403", description = "Current user is not administrator"),
        @ApiResponse(
            responseCode = "404",
            description = "Celestial body with given id can't be find"),
        @ApiResponse(responseCode = "409", description = "Name is already used")
      })
  @PatchMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<CelestialBodyDto> update(
      @PathVariable Long id, @RequestBody UpdateCelestialBodyDto dto) {
    var celestialBody = celestialBodyService.update(id, dto);
    return ResponseEntity.ok(celestialBody);
  }

  @Operation(summary = "Delete a celestial body")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Celestial body deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Parameter \"id\" is not a valid Long"),
        @ApiResponse(responseCode = "403", description = "Current user is not administrator"),
        @ApiResponse(
            responseCode = "404",
            description = "Celestial body with given id can't be find"),
      })
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    celestialBodyService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
