package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.*;
import fr.openobservatory.backend.entities.PushSubscriptionEntity;
import fr.openobservatory.backend.exceptions.UnavailableUserException;
import fr.openobservatory.backend.exceptions.UnknownUserException;
import fr.openobservatory.backend.providers.PushProvider;
import fr.openobservatory.backend.repositories.PushSubscriptionRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import java.time.Instant;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PushSubscriptionService {

  private final PushProvider pushProvider;
  private final PushSubscriptionRepository pushSubscriptionRepository;
  private final UserRepository userRepository;

  // ---

  /** Retrieves the public key used to sign push notification messages. */
  public String getPublicKey() {
    return pushProvider.publicKey();
  }

  /**
   * Sends a push notification to the targeted user.
   *
   * @param targetUsername User to send a push notification to.
   * @param payload Payload to send in the push notification.
   */
  public void sendTo(String targetUsername, PushNotificationDto payload) {
    var target =
        userRepository
            .findByUsernameIgnoreCase(targetUsername)
            .orElseThrow(UnknownUserException::new);
    var subscriptions = pushSubscriptionRepository.findAllByUser(target);
    for (var s : subscriptions) {
      try {
        pushProvider.send(new PushProvider.PushMessage(
                s.getEndpoint(),
                s.getAuth(),
                s.getP256dh(),
                payload
        ));
      } catch (PushProvider.PushMessageException ignored) {
        pushSubscriptionRepository.delete(s);
      }
    }
  }

  /**
   * Creates a new push subscription.
   *
   * @param issuerUsername User that issued the action.
   * @param dto Payload containing the desired subscription.
   * @param userAgent User agent extracted from the subscription request.
   */
  public void subscribe(String issuerUsername, SubscribeNotificationsDto dto, String userAgent) {
    var issuer =
        userRepository
            .findByUsernameIgnoreCase(issuerUsername)
            .orElseThrow(UnavailableUserException::new);
    var entity =
        new PushSubscriptionEntity()
            .setAuth(dto.getAuth())
            .setEndpoint(dto.getEndpoint())
            .setP256dh(dto.getP256dh())
            .setUser(issuer)
            .setUserAgent(userAgent)
            .setCreatedAt(Instant.now());
    pushSubscriptionRepository.save(entity);
  }
}
