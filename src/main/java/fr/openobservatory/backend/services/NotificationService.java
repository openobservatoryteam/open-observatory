package fr.openobservatory.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.openobservatory.backend.configuration.PushServiceConfiguration;
import fr.openobservatory.backend.dto.PushKeyDto;
import fr.openobservatory.backend.dto.SendNotificationDto;
import fr.openobservatory.backend.dto.SubscribeNotificationsDto;
import fr.openobservatory.backend.dto.UnsubscribeNotificationsDto;
import fr.openobservatory.backend.entities.NotificationEntity;
import java.util.Map;
import lombok.AllArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class NotificationService {

  private final ModelMapper modelMapper;
  private final ObjectMapper objectMapper;
  private final PushService pushService;
  private final Map<String, NotificationEntity> subscriptions;

  // ---

  /** Public key of the push service. */
  public PushKeyDto getKey() {
    var dto = new PushKeyDto();
    dto.setKey(PushServiceConfiguration.PUBLIC_KEY);
    return dto;
  }

  /**
   * Sends a notification to all subscribers.
   *
   * @param dto Notification data.
   */
  public void send(SendNotificationDto dto) {
    subscriptions
        .values()
        .forEach(
            s -> {
              try {
                var notification =
                    Notification.builder()
                        .endpoint(s.getEndpoint())
                        .payload(objectMapper.writeValueAsBytes(dto))
                        .userAuth(s.getAuth())
                        .userPublicKey(s.getP256dh())
                        .build();
                pushService.send(notification);
              } catch (Exception e) {
                throw new RuntimeException(e);
              }
            });
  }

  /**
   * Registers a new subscription to the list.
   *
   * @param dto Information about the subscription.
   */
  public void subscribe(SubscribeNotificationsDto dto) {
    var entity = modelMapper.map(dto, NotificationEntity.class);
    subscriptions.put(entity.getEndpoint(), entity);
  }

  /**
   * Unregisters a subscription from the list.
   *
   * @param dto Endpoint of the subscriber.
   */
  public void unsubscribe(UnsubscribeNotificationsDto dto) {
    subscriptions.remove(dto.getEndpoint());
  }
}
