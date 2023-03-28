package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.PushKeyDto;
import fr.openobservatory.backend.dto.SendNotificationDto;
import fr.openobservatory.backend.dto.SubscribeNotificationsDto;
import fr.openobservatory.backend.dto.UnsubscribeNotificationsDto;
import fr.openobservatory.backend.services.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RequestMapping("/notifications")
@RestController
public class NotificationController {

  private final NotificationService notificationService;

  // ---

  @GetMapping("/key")
  public ResponseEntity<PushKeyDto> key() {
    var dto = notificationService.getKey();
    return ResponseEntity.ok(dto);
  }

  @PostMapping("/send")
  public ResponseEntity<Void> send(@RequestBody SendNotificationDto dto) {
    notificationService.send(dto);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/subscribe")
  public ResponseEntity<Void> subscribe(@RequestBody SubscribeNotificationsDto dto) {
    notificationService.subscribe(dto);
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/unsubscribe")
  public ResponseEntity<Void> unsubscribe(@RequestBody UnsubscribeNotificationsDto dto) {
    notificationService.unsubscribe(dto);
    return ResponseEntity.noContent().build();
  }
}
