package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.CelestialBodyDto;
import fr.openobservatory.backend.dto.CreateCelestialBodyDto;
import fr.openobservatory.backend.dto.SearchResultsDto;
import fr.openobservatory.backend.dto.UpdateCelestialBodyDto;
import fr.openobservatory.backend.services.CelestialBodyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
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
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<SearchResultsDto<CelestialBodyDto>> search(
      @RequestParam(required = false, defaultValue = "10") Integer limit,
      @RequestParam(required = false, defaultValue = "0") Integer page) {
    return ResponseEntity.ok(celestialBodyService.search(page, limit));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CelestialBodyDto> findById(@PathVariable Long id) {
    return ResponseEntity.of(celestialBodyService.findById(id));
  }

  @PostMapping
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<CelestialBodyDto> create(@RequestBody @Valid CreateCelestialBodyDto dto) {
    var celestialBody = celestialBodyService.create(dto);
    return new ResponseEntity<>(celestialBody, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<CelestialBodyDto> update(
      @PathVariable Long id, @RequestBody @Valid UpdateCelestialBodyDto dto) {
    var celestialBody = celestialBodyService.update(id, dto);
    return new ResponseEntity<>(celestialBody, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<?> delete(@PathVariable Long id) {
    celestialBodyService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
