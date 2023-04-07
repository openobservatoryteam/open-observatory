package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.*;
import fr.openobservatory.backend.services.PushSubscriptionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/push")
@RestController
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
      HttpServletRequest request,
      Authentication authentication,
      @RequestBody SubscribeNotificationsDto dto) {
    var userAgent = request.getHeader("User-Agent");
    pushSubscriptionService.subscribe(authentication.getName(), dto, userAgent);
    return ResponseEntity.noContent().build();
  }
}
