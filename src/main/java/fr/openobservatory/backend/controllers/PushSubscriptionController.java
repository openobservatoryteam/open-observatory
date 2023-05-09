package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.input.SubscribeNotificationsDto;
import fr.openobservatory.backend.services.PushSubscriptionService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/push")
@RestController
@Hidden
public class PushSubscriptionController {

  private final PushSubscriptionService pushSubscriptionService;

  // ---

  @GetMapping("/public-key")
  public ResponseEntity<String> getPublicKey() {
    var dto = pushSubscriptionService.getPublicKey();
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/subscribe")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> subscribe(
      Authentication authentication, @RequestBody SubscribeNotificationsDto dto) {
    pushSubscriptionService.subscribe(authentication.getName(), dto);
    return ResponseEntity.noContent().build();
  }
}
