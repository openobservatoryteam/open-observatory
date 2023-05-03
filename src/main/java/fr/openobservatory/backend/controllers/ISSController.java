package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.output.ISSPositionDto;
import fr.openobservatory.backend.services.ISSService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/iss")
@RestController
public class ISSController {

  private ISSService issService;

  // ---

  @GetMapping("/positions")
  public ResponseEntity<List<ISSPositionDto>> findISSPositions() {
    var positions = issService.findISSPositions();
    return ResponseEntity.ok(positions);
  }
}
