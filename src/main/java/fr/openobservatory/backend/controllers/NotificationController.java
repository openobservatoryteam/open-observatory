package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.SubscribeNotificationsDto;
import fr.openobservatory.backend.dto.SendNotificationDto;
import fr.openobservatory.backend.dto.UnsubscribeNotificationsDto;
import fr.openobservatory.backend.services.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RequestMapping("/notifications")
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    // ---

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
