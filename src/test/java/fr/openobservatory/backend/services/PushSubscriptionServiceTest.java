package fr.openobservatory.backend.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

import fr.openobservatory.backend.dto.PushNotificationDto;
import fr.openobservatory.backend.dto.SubscribeNotificationsDto;
import fr.openobservatory.backend.entities.PushSubscriptionEntity;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.providers.PushProvider;
import fr.openobservatory.backend.repositories.PushSubscriptionRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PushSubscriptionServiceTest {

  @Mock PushProvider pushProvider;

  @Mock PushSubscriptionRepository pushSubscriptionRepository;

  @Mock UserRepository userRepository;

  @InjectMocks PushSubscriptionService pushSubscriptionService;

  // --- PushSubscriptionService#getPublicKey

  @DisplayName("PushSubscriptionService#getPublicKey should return public key")
  @Test
  void getPublicKey_should_return_public_key() {
    // Given
    var publicKey = "HelloWorld123!";

    // When
    when(pushProvider.publicKey()).thenReturn(publicKey);
    var key = pushSubscriptionService.getPublicKey();

    // Then
    assertThat(key).isEqualTo(publicKey);
  }

  // --- PushSubscriptionService#sendTo

  @DisplayName("PushSubscriptionService#sendTo should send notification")
  @Test
  void sendTo_should_send_notification() throws PushProvider.PushMessageException {
    // Given
    var target = new UserEntity().setUsername("hello");
    var subscriptions =
        List.of(
            new PushSubscriptionEntity().setUser(target),
            new PushSubscriptionEntity().setUser(target));

    // When
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    when(pushSubscriptionRepository.findAllByUser(target)).thenReturn(subscriptions);
    pushSubscriptionService.sendTo(target.getUsername(), new PushNotificationDto().setCode("AAAA"));

    // Then
    verify(pushProvider, times(subscriptions.size())).send(isA(PushProvider.PushMessage.class));
  }

  @DisplayName(
      "PushSubscriptionService#sendTo should delete subscription when provider failed to send")
  @Test
  void sendTo_should_delete_subscription_when_provider_failed_to_send()
      throws PushProvider.PushMessageException {
    // Given
    var target = new UserEntity().setUsername("hello");
    var subscriptions =
        List.of(
            new PushSubscriptionEntity().setUser(target),
            new PushSubscriptionEntity().setUser(target));

    // When
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    when(pushSubscriptionRepository.findAllByUser(target)).thenReturn(subscriptions);
    doThrow(new PushProvider.PushMessageException())
        .when(pushProvider)
        .send(isA(PushProvider.PushMessage.class));
    pushSubscriptionService.sendTo(target.getUsername(), new PushNotificationDto().setCode("AAAA"));

    // Then
    verify(pushProvider, times(subscriptions.size())).send(isA(PushProvider.PushMessage.class));
    verify(pushSubscriptionRepository, times(subscriptions.size()))
        .delete(isA(PushSubscriptionEntity.class));
  }

  // --- PushSubscriptionService#subscribe

  @DisplayName("PushSubscriptionService#subscribe should subscribe user")
  @Test
  void subscribe_should_subscribe_user() {
    // Given
    var issuer = new UserEntity().setUsername("hello");
    var subscription = new SubscribeNotificationsDto().setAuth("a").setP256dh("b").setEndpoint("c");

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    pushSubscriptionService.subscribe(issuer.getUsername(), subscription, "Some agent");

    // Then
    verify(pushSubscriptionRepository, times(1)).save(isA(PushSubscriptionEntity.class));
  }
}
