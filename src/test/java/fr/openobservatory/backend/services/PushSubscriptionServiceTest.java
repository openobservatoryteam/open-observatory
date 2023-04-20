package fr.openobservatory.backend.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.openobservatory.backend.dto.input.PushNotificationDto;
import fr.openobservatory.backend.dto.input.SubscribeNotificationsDto;
import fr.openobservatory.backend.entities.PushSubscriptionEntity;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.exceptions.ValidationException;
import fr.openobservatory.backend.providers.PushProvider;
import fr.openobservatory.backend.providers.PushProvider.PushMessageException;
import fr.openobservatory.backend.repositories.PushSubscriptionRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class PushSubscriptionServiceTest {

  @Spy ModelMapper modelMapper = new ModelMapper();
  @Mock PushProvider pushProvider;
  @Mock PushSubscriptionRepository pushSubscriptionRepository;
  @Mock UserRepository userRepository;
  @Spy Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
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
  void sendTo_should_send_notification() throws PushMessageException {
    // Given
    var target = UserEntity.builder().username("target").build();
    var subscriptions =
        List.of(
            PushSubscriptionEntity.builder().user(target).build(),
            PushSubscriptionEntity.builder().user(target).build());

    // When
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    when(pushSubscriptionRepository.findAllByUser(target)).thenReturn(subscriptions);
    pushSubscriptionService.sendTo(
        target.getUsername(), PushNotificationDto.builder().code("AAA").build());

    // Then
    verify(pushProvider, times(subscriptions.size())).send(isA(PushProvider.PushMessage.class));
  }

  @DisplayName(
      "PushSubscriptionService#sendTo should delete subscription when provider failed to send")
  @Test
  void sendTo_should_delete_subscription_when_provider_failed_to_send()
      throws PushMessageException {
    // Given
    var target = UserEntity.builder().username("target").build();
    var subscriptions =
        List.of(
            PushSubscriptionEntity.builder().user(target).build(),
            PushSubscriptionEntity.builder().user(target).build());

    // When
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    when(pushSubscriptionRepository.findAllByUser(target)).thenReturn(subscriptions);
    doThrow(new PushMessageException())
        .when(pushProvider)
        .send(isA(PushProvider.PushMessage.class));
    pushSubscriptionService.sendTo(
        target.getUsername(), PushNotificationDto.builder().code("AAAA").build());

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
    var issuer = UserEntity.builder().username("issuer").build();
    var subscription =
        SubscribeNotificationsDto.builder()
            .auth("RIyPqiw8O1zqZMzUAebFprFh")
            .p256dh(
                "9OzRC3OuCaFwpWclDktXnYcQlJwtQ9gzUa4C2stJAGcBqB45gyFKeCoZQxYkLCYsN8u4Ug163JpkipllJzPVVIUa")
            .endpoint("https://test.mozilla.com/some-push-endpoint")
            .build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    pushSubscriptionService.subscribe(issuer.getUsername(), subscription);

    // Then
    verify(pushSubscriptionRepository, times(1)).save(isA(PushSubscriptionEntity.class));
  }

  @DisplayName("PushSubscriptionService#subscribe should throw when dto is invalid")
  @Test
  void subscribe_should_throw_when_dto_is_invalid() {
    // Given
    var dto =
        SubscribeNotificationsDto.builder().auth(null).p256dh(null).endpoint("not-an-url").build();

    // When
    ThrowingCallable action = () -> pushSubscriptionService.subscribe("issuer", dto);

    // Then
    assertThatThrownBy(action)
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue(
            "violations", Set.of("auth.required", "p256dh.required", "endpoint.url"));
  }
}
