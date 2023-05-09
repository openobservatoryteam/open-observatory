package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.output.ISSPositionDto;
import fr.openobservatory.backend.services.ISSService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/iss")
@RestController
@Tag(name = "ISS routes", description = "All ISS related routes")
public class ISSController {

  private ISSService issService;

  // ---
  @Operation(summary = "Get ISS position and it's trajectory")
  @ApiResponse(responseCode = "200", description = "Return ISS current and future positions")
  @GetMapping("/positions")
  public ResponseEntity<List<ISSPositionDto>> findISSPositions() {
    var positions = issService.findISSPositions();
    return ResponseEntity.ok(positions);
  }
}
