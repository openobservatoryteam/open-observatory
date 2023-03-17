package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.CelestialBodyDto;
import fr.openobservatory.backend.dto.CreateCelestialBodyDto;
import fr.openobservatory.backend.dto.SearchResultsDto;
import fr.openobservatory.backend.dto.UpdateCelestialBodyDto;
import fr.openobservatory.backend.services.CelestialBodyService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/celestial-bodies")
public class CelestialBodyController {

  private final CelestialBodyService celestialBodyService;
  private final ModelMapper modelMapper;

  // ---

  @GetMapping
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SearchResultsDto<CelestialBodyDto>> search(
      @RequestParam(required = false, defaultValue = "10") Integer limit,
      @RequestParam(required = false, defaultValue = "0") Integer page) {
    var celestialBodies =
        celestialBodyService
            .search(page, limit)
            .map(c -> modelMapper.map(c, CelestialBodyDto.class));
    var searchResult = SearchResultsDto.from(celestialBodies);
    return ResponseEntity.ok(searchResult);
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<CelestialBodyDto> findById(@PathVariable Long id) {
    var celestialBody = modelMapper.map(celestialBodyService.findById(id), CelestialBodyDto.class);
    return ResponseEntity.ok(celestialBody);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<CelestialBodyDto> create(@RequestBody @Valid CreateCelestialBodyDto dto) {
    var celestialBody = modelMapper.map(celestialBodyService.create(dto), CelestialBodyDto.class);
    return new ResponseEntity<>(celestialBody, HttpStatus.CREATED);
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<CelestialBodyDto> update(
      @PathVariable Long id, @RequestBody @Valid UpdateCelestialBodyDto dto) {
    var celestialBody =
        modelMapper.map(celestialBodyService.update(id, dto), CelestialBodyDto.class);
    return new ResponseEntity<>(celestialBody, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    celestialBodyService.delete(id);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
