package fr.openobservatory.backend.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.openobservatory.backend.configuration.PushServiceConfiguration;
import fr.openobservatory.backend.dto.*;
import fr.openobservatory.backend.entities.PushSubscriptionEntity;
import fr.openobservatory.backend.exceptions.UnavailableUserException;
import fr.openobservatory.backend.exceptions.UnknownUserException;
import fr.openobservatory.backend.repositories.PushSubscriptionRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutionException;
import lombok.AllArgsConstructor;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.jose4j.lang.JoseException;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PushSubscriptionService {

  private final ModelMapper modelMapper;
  private final ObjectMapper objectMapper;
  private final PushService pushService;
  private final PushSubscriptionRepository pushSubscriptionRepository;
  private final UserRepository userRepository;

  // ---

  /**
   * Finds all active push subscriptions for the targeted user.
   *
   * @param targetUsername User to retrieve subscriptions of.
   */
  public List<PushSubscriptionDto> findSubscriptionsByUser(String targetUsername) {
    var target =
        userRepository
            .findByUsernameIgnoreCase(targetUsername)
            .orElseThrow(UnknownUserException::new);
    return pushSubscriptionRepository.findAllByUser(target).stream()
        .map(p -> modelMapper.map(p, PushSubscriptionDto.class))
        .toList();
  }

  /** Retrieves the public key used to sign push notification messages. */
  public PushSubscriptionKeyDto getSubscriptionKey() {
    return new PushSubscriptionKeyDto().setKey(PushServiceConfiguration.PUBLIC_KEY);
  }

  /**
   * Sends a push notification to all users.
   *
   * @param payload Payload to send in the push notification.
   */
  public void sendAll(PushNotificationDto payload)
      throws JoseException, GeneralSecurityException, IOException, ExecutionException,
          InterruptedException {
    var subscriptions = pushSubscriptionRepository.findAll();
    for (PushSubscriptionEntity s : subscriptions) {
      send(s, payload);
    }
  }

  /**
   * Sends a push notification to the targeted user.
   *
   * @param targetUsername User to send a push notification to.
   * @param payload Payload to send in the push notification.
   */
  public void sendTo(String targetUsername, PushNotificationDto payload)
      throws JoseException, GeneralSecurityException, IOException, ExecutionException,
          InterruptedException {
    var target =
        userRepository
            .findByUsernameIgnoreCase(targetUsername)
            .orElseThrow(UnknownUserException::new);
    var subscriptions = pushSubscriptionRepository.findAllByUser(target);
    for (PushSubscriptionEntity s : subscriptions) {
      send(s, payload);
    }
  }

  /**
   * Creates a new push subscription.
   *
   * @param issuerUsername User that issued the action.
   * @param dto Payload containing the desired subscription.
   */
  public void subscribe(String issuerUsername, SubscribeNotificationsDto dto) {
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
            .setCreatedAt(Instant.now());
    pushSubscriptionRepository.save(entity);
  }

  /**
   * Cancels the given push subscription.
   *
   * @param issuerUsername User that issued the action.
   * @param dto Payload containing the targeted subscription.
   */
  public void unsubscribe(String issuerUsername, UnsubscribeNotificationsDto dto) {
    var issuer =
        userRepository
            .findByUsernameIgnoreCase(issuerUsername)
            .orElseThrow(UnavailableUserException::new);
    pushSubscriptionRepository
        .findById(dto.getEndpoint())
        .ifPresent(
            subscription -> {
              if (subscription.getUser().equals(issuer))
                pushSubscriptionRepository.delete(subscription);
            });
  }

  // ---

  /**
   * Sends a push notification to the subscription that contains the given payload.
   *
   * @param subscription Subscription to send a push notification to.
   * @param payload Payload to send in the push notification.
   */
  private void send(PushSubscriptionEntity subscription, PushNotificationDto payload)
      throws IOException, GeneralSecurityException, JoseException, ExecutionException,
          InterruptedException {
    var serializedPayload = objectMapper.writeValueAsBytes(payload);
    var notification =
        Notification.builder()
            .endpoint(subscription.getEndpoint())
            .payload(serializedPayload)
            .userAuth(subscription.getAuth())
            .userPublicKey(subscription.getP256dh())
            .build();
    var response = pushService.send(notification);
    if (response.getStatusLine().getStatusCode() != HttpStatus.CREATED.value())
      pushSubscriptionRepository.delete(subscription);
  }
}