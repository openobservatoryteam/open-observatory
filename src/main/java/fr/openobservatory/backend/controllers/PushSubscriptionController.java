package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.*;
import fr.openobservatory.backend.services.PushSubscriptionService;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import org.jose4j.lang.JoseException;
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

  @PostMapping("/send-all")
  // @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<Void> send(@RequestBody PushNotificationDto dto)
      throws JoseException, GeneralSecurityException, IOException, ExecutionException,
          InterruptedException {
    pushSubscriptionService.sendAll(dto);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/send-to/{targetUsername}")
  // @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<Void> sendTo(
      @PathVariable String targetUsername, @RequestBody PushNotificationDto dto)
      throws JoseException, GeneralSecurityException, IOException, ExecutionException,
          InterruptedException {
    pushSubscriptionService.sendTo(targetUsername, dto);
    return ResponseEntity.noContent().build();
  }

  // ---

  @GetMapping("/public-key")
  public ResponseEntity<PushSubscriptionKeyDto> key() {
    var dto = pushSubscriptionService.getSubscriptionKey();
    return ResponseEntity.ok(dto);
  }

  @GetMapping("/subscriptions")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<PushSubscriptionDto>> findSubscriptionsByUser(
      Authentication authentication) {
    var subscription = pushSubscriptionService.findSubscriptionsByUser(authentication.getName());
    return ResponseEntity.ok(subscription);
  }

  @PostMapping("/subscribe")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> subscribe(
      Authentication authentication, @RequestBody SubscribeNotificationsDto dto) {
    pushSubscriptionService.subscribe(authentication.getName(), dto);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/unsubscribe")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> unsubscribe(
      Authentication authentication, @RequestBody UnsubscribeNotificationsDto dto) {
    pushSubscriptionService.unsubscribe(authentication.getName(), dto);
    return ResponseEntity.noContent().build();
  }
}
