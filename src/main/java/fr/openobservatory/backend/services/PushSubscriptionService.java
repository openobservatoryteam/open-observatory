package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.input.PushNotificationDto;
import fr.openobservatory.backend.dto.input.SubscribeNotificationsDto;
import fr.openobservatory.backend.entities.PushSubscriptionEntity;
import fr.openobservatory.backend.exceptions.UnavailableUserException;
import fr.openobservatory.backend.exceptions.UnknownUserException;
import fr.openobservatory.backend.exceptions.ValidationException;
import fr.openobservatory.backend.providers.PushProvider;
import fr.openobservatory.backend.providers.PushProvider.PushMessage;
import fr.openobservatory.backend.providers.PushProvider.PushMessageException;
import fr.openobservatory.backend.repositories.PushSubscriptionRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class PushSubscriptionService {

  private final ModelMapper modelMapper;
  private final PushProvider pushProvider;
  private final PushSubscriptionRepository pushSubscriptionRepository;
  private final UserRepository userRepository;
  private final Validator validator;

  // ---

  /** Retrieves the public key used to sign push notification messages. */
  public String getPublicKey() {
    return pushProvider.publicKey();
  }

  /**
   * Sends a push notification to the targeted user.
   *
   * @param targetUsername User to send a push notification to.
   * @param dto Payload to send in the push notification.
   */
  public void sendTo(String targetUsername, PushNotificationDto dto) {
    var target =
        userRepository
            .findByUsernameIgnoreCase(targetUsername)
            .orElseThrow(UnknownUserException::new);
    var subscriptions = pushSubscriptionRepository.findAllByUser(target);
    for (var s : subscriptions) {
      try {
        pushProvider.send(new PushMessage(s.getEndpoint(), s.getAuth(), s.getP256dh(), dto));
      } catch (PushMessageException ignored) {
        pushSubscriptionRepository.delete(s);
      }
    }
  }

  /**
   * Creates a new push subscription.
   *
   * @param issuerUsername User that issued the action.
   * @param dto Payload containing the desired subscription.
   */
  public void subscribe(String issuerUsername, SubscribeNotificationsDto dto) {
    var violations = validator.validate(dto);
    if (!violations.isEmpty()) throw new ValidationException(violations);
    var issuer =
        userRepository
            .findByUsernameIgnoreCase(issuerUsername)
            .orElseThrow(UnavailableUserException::new);
    var subscription = modelMapper.map(dto, PushSubscriptionEntity.class);
    subscription.setUser(issuer);
    pushSubscriptionRepository.save(subscription);
  }
}
