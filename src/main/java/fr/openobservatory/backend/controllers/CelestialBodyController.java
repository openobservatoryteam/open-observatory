package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.input.CreateCelestialBodyDto;
import fr.openobservatory.backend.dto.input.PaginationDto;
import fr.openobservatory.backend.dto.input.UpdateCelestialBodyDto;
import fr.openobservatory.backend.dto.output.CelestialBodyDto;
import fr.openobservatory.backend.dto.output.SearchResultsDto;
import fr.openobservatory.backend.services.CelestialBodyService;
import java.net.URI;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/celestial-bodies")
public class CelestialBodyController {

  private final CelestialBodyService celestialBodyService;

  // ---

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SearchResultsDto<CelestialBodyDto>> search(PaginationDto dto) {
    var celestialBodies = celestialBodyService.search(dto);
    return ResponseEntity.ok(celestialBodies);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<CelestialBodyDto> findById(@PathVariable Long id) {
    var celestialBody = celestialBodyService.findById(id);
    return ResponseEntity.ok(celestialBody);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<CelestialBodyDto> create(@RequestBody CreateCelestialBodyDto dto) {
    var celestialBody = celestialBodyService.create(dto);
    return ResponseEntity.created(URI.create("/celestial-bodies/" + celestialBody.getId()))
        .body(celestialBody);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<CelestialBodyDto> update(
      @PathVariable Long id, @RequestBody UpdateCelestialBodyDto dto) {
    var celestialBody = celestialBodyService.update(id, dto);
    return ResponseEntity.ok(celestialBody);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    celestialBodyService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
