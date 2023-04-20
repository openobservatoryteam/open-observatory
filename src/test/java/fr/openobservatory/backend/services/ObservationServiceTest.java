package fr.openobservatory.backend.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

import fr.openobservatory.backend.dto.input.*;
import fr.openobservatory.backend.entities.CelestialBodyEntity;
import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.ObservationEntity.Visibility;
import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity.VoteType;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.entities.UserEntity.Type;
import fr.openobservatory.backend.exceptions.ObservationNotEditableException;
import fr.openobservatory.backend.exceptions.ValidationException;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import fr.openobservatory.backend.repositories.ObservationRepository;
import fr.openobservatory.backend.repositories.ObservationVoteRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ObservationServiceTest {

  @Mock AchievementService achievementService;
  @Mock CelestialBodyRepository celestialBodyRepository;
  @Spy ModelMapper modelMapper = new ModelMapper();
  @Mock ObservationRepository observationRepository;
  @Mock ObservationVoteRepository observationVoteRepository;
  @Mock PushSubscriptionService pushSubscriptionService;
  @Mock UserRepository userRepository;
  @Spy Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  @InjectMocks ObservationService observationService;

  // --- ObservationService#create

  @DisplayName("ObservationService#create should create an observation")
  @Test
  void create_should_create_an_observation() {
    // Given
    var issuer = UserEntity.builder().username("issuer").build();
    var celestialBody =
        CelestialBodyEntity.builder().id(2L).name("Neptune").validityTime(13).build();
    var dto =
        CreateObservationDto.builder()
            .celestialBodyId(celestialBody.getId())
            .latitude(12.5)
            .longitude(49.3)
            .orientation(30)
            .visibility(Visibility.VISIBLE)
            .timestamp(Instant.ofEpochSecond(1355314332L))
            .build();
    var notifiableUsers =
        Set.of(
            issuer,
            UserEntity.builder()
                .username("toNotify")
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .notificationRadius(5)
                .positionAt(Instant.now())
                .build(),
            UserEntity.builder()
                .username("toNotNotify1")
                .latitude(-90.0)
                .longitude(dto.getLongitude())
                .notificationRadius(5)
                .positionAt(Instant.now())
                .build(),
            UserEntity.builder()
                .username("toNotNotify2")
                .latitude(90.0)
                .longitude(dto.getLongitude())
                .notificationRadius(5)
                .positionAt(Instant.now())
                .build(),
            UserEntity.builder()
                .username("toNotNotify3")
                .latitude(dto.getLatitude())
                .longitude(-180.0)
                .notificationRadius(5)
                .positionAt(Instant.now())
                .build(),
            UserEntity.builder()
                .username("toNotNotify4")
                .latitude(dto.getLatitude())
                .longitude(180.0)
                .notificationRadius(5)
                .positionAt(Instant.now())
                .build());

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(userRepository
            .findAllByNotificationEnabledIsTrueAndLatitudeIsNotNullAndLongitudeIsNotNullAndPositionAtIsGreaterThanEqual(
                isA(Instant.class)))
        .thenReturn(notifiableUsers);
    when(celestialBodyRepository.findById(celestialBody.getId()))
        .thenReturn(Optional.of(celestialBody));
    when(observationRepository.save(Mockito.isA(ObservationEntity.class)))
        .thenAnswer(a -> a.getArgument(0));
    var observation = observationService.create(issuer.getUsername(), dto);

    // Then
    assertThat(observation.getAuthor().getUsername()).isEqualTo(issuer.getUsername());
    assertThat(observation.getCelestialBody().getId()).isEqualTo(celestialBody.getId());
    assertThat(observation.getDescription()).isEqualTo(dto.getDescription());
    assertThat(observation.getLatitude()).isEqualTo(dto.getLatitude());
    assertThat(observation.getLongitude()).isEqualTo(dto.getLongitude());
    assertThat(observation.getOrientation()).isEqualTo(dto.getOrientation());
    assertThat(observation.getVisibility()).isEqualTo(dto.getVisibility());
    assertThat(observation.getTimestamp()).isEqualTo(dto.getTimestamp());
    assertThat(observation.getCurrentVote()).isNull();
    assertThat(observation.isExpired()).isTrue();
    assertThat(observation.getKarma()).isZero();
  }

  @DisplayName("ObservationService#create should throw when dto is invalid")
  @Test
  void create_should_throw_when_dto_is_invalid() {
    // Given
    var dto =
        CreateObservationDto.builder()
            .celestialBodyId(-1L)
            .latitude(1000.44)
            .longitude(-390.90)
            .orientation(361)
            .timestamp(Instant.now().plus(5, ChronoUnit.HOURS))
            .visibility(null)
            .build();

    // When
    ThrowingCallable action = () -> observationService.create("issuer", dto);

    // Then
    assertThatThrownBy(action)
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue(
            "violations",
            Set.of(
                "celestialBodyId.positive",
                "latitude.range",
                "longitude.range",
                "orientation.range",
                "timestamp.pastOrPresent",
                "visibility.required"));
  }

  // --- ObservationService#findAllNearby

  @DisplayName(
      "ObservationService#findAllNearby should find all observations nearby the given point")
  @Test
  void findAllNearby_should_find_all_observations_nearby_the_given_point() {
    // Given
    var observation =
        ObservationEntity.builder()
            .id(3302L)
            .celestialBody(
                CelestialBodyEntity.builder().id(12L).name("Neptune").validityTime(12).build())
            .latitude(49.3)
            .longitude(30.2)
            .orientation(30)
            .visibility(Visibility.VISIBLE)
            .timestamp(Instant.now())
            .build();
    var dto =
        FindNearbyObservationsDto.builder().latitude(49.2).longitude(30.1).radius(100.0).build();

    // When
    when(observationRepository.findAllNearby(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
        .thenReturn(Set.of(observation));
    var observations = observationService.findAllNearby(dto);

    // Then
    assertThat(observations).hasSize(1);
    assertThat(observations.get(0).getId()).isEqualTo(observation.getId());
  }

  // --- ObservationService#findById

  @DisplayName("ObservationService#findById should find an observation")
  @Test
  void findById_should_find_an_observation() {
    // Given
    var entity =
        ObservationEntity.builder()
            .id(33L)
            .celestialBody(CelestialBodyEntity.builder().validityTime(3).build())
            .timestamp(Instant.ofEpochSecond(1355314332L))
            .votes(
                Set.of(
                    ObservationVoteEntity.builder()
                        .vote(VoteType.UPVOTE)
                        .user(UserEntity.builder().username("voter").build())
                        .build()))
            .build();

    // When
    when(observationRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
    var observation = observationService.findById(entity.getId(), null);

    // Then
    assertThat(observation.getId()).isEqualTo(entity.getId());
    assertThat(observation.getCurrentVote()).isNull();
    assertThat(observation.isExpired()).isTrue();
    assertThat(observation.getKarma()).isOne();
  }

  @DisplayName("ObservationService#findById should find an observation (issuer exists)")
  @Test
  void findById_should_find_an_observation_with_issuer() {
    // Given
    var issuer = UserEntity.builder().username("issuer").build();
    var entity =
        ObservationEntity.builder()
            .id(33L)
            .celestialBody(CelestialBodyEntity.builder().validityTime(3).build())
            .timestamp(Instant.ofEpochSecond(1355314332L))
            .votes(
                Set.of(ObservationVoteEntity.builder().vote(VoteType.UPVOTE).user(issuer).build()))
            .build();

    // When
    when(observationRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    var observation = observationService.findById(entity.getId(), issuer.getUsername());

    // Then
    assertThat(observation.getId()).isEqualTo(entity.getId());
    assertThat(observation.getCurrentVote()).isEqualTo(VoteType.UPVOTE);
    assertThat(observation.isExpired()).isTrue();
    assertThat(observation.getKarma()).isOne();
  }

  // --- ObservationService#search

  @DisplayName("ObservationService#search should search observations")
  @Test
  void search_should_search_observations() {
    // Given
    var issuer = UserEntity.builder().username("issuer").build();
    var dto = PaginationDto.builder().itemsPerPage(1).page(0).build();
    var observation =
        ObservationEntity.builder()
            .id(33L)
            .celestialBody(CelestialBodyEntity.builder().validityTime(3).build())
            .timestamp(Instant.ofEpochSecond(1355314332L))
            .build();

    // When
    when(observationRepository.findAllByOrderByTimestampDesc(isA(Pageable.class)))
        .thenReturn(
            new PageImpl<>(
                List.of(observation),
                Pageable.ofSize(dto.getItemsPerPage()).withPage(dto.getPage()),
                1));
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    var observations = observationService.search(dto, issuer.getUsername());

    // Then
    assertThat(observations.getItemsPerPage()).isOne();
    assertThat(observations.getItemCount()).isOne();
    assertThat(observations.getPage()).isZero();
    assertThat(observations.getPageCount()).isOne();
    assertThat(observations.getData().get(0).getId()).isEqualTo(observation.getId());
  }

  @DisplayName("ObservationService#search should throw when dto is invalid")
  @Test
  void search_should_throw_when_dto_is_invalid() {
    // Given
    var dto = PaginationDto.builder().itemsPerPage(1039).page(-3).build();

    // When
    ThrowingCallable action = () -> observationService.search(dto, "issuer");

    // Then
    assertThatThrownBy(action)
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("violations", Set.of("itemsPerPage.range", "page.range"));
  }

  // --- ObservationService#submitVote

  @DisplayName("ObservationService#submitVote should submit vote")
  @Test
  void submitVote_should_submit_vote() {
    // Given
    var issuer = UserEntity.builder().username("issuer").build();
    var observation = ObservationEntity.builder().id(1L).build();
    var dto = SubmitVoteDto.builder().vote(VoteType.UPVOTE).build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(observationRepository.findById(observation.getId())).thenReturn(Optional.of(observation));
    when(observationVoteRepository.findByObservationAndUser(observation, issuer))
        .thenReturn(Optional.empty());
    when(observationVoteRepository.save(Mockito.isA(ObservationVoteEntity.class)))
        .thenAnswer(a -> a.getArgument(0));
    observationService.submitVote(observation.getId(), dto, issuer.getUsername());

    // Then
    verify(observationVoteRepository, times(1)).save(isA(ObservationVoteEntity.class));
  }

  @DisplayName("ObservationService#submitVote should delete existing vote")
  @Test
  void submitVote_should_delete_existing_vote() {
    // Given
    var issuer = UserEntity.builder().username("issuer").build();
    var observation = ObservationEntity.builder().id(1L).build();
    var currentVote = ObservationVoteEntity.builder().vote(VoteType.UPVOTE).build();
    var dto = SubmitVoteDto.builder().build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(observationRepository.findById(observation.getId())).thenReturn(Optional.of(observation));
    when(observationVoteRepository.findByObservationAndUser(observation, issuer))
        .thenReturn(Optional.of(currentVote));
    observationService.submitVote(observation.getId(), dto, issuer.getUsername());

    // Then
    verify(observationVoteRepository, times(1)).delete(isA(ObservationVoteEntity.class));
  }

  @DisplayName("ObservationService#submitVote should do nothing")
  @Test
  void submitVote_should_do_nothing() {
    // Given
    var issuer = UserEntity.builder().username("issuer").build();
    var observation = ObservationEntity.builder().id(1L).build();
    var dto = SubmitVoteDto.builder().build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(observationRepository.findById(observation.getId())).thenReturn(Optional.of(observation));
    when(observationVoteRepository.findByObservationAndUser(observation, issuer))
        .thenReturn(Optional.empty());
    observationService.submitVote(observation.getId(), dto, issuer.getUsername());
  }

  // --- ObservationService#update

  @DisplayName("ObservationService#update should update the targeted observation")
  @Test
  void update_should_update_the_targeted_observation() {
    // Given
    var issuer = UserEntity.builder().username("Thomas").build();
    var entity =
        ObservationEntity.builder()
            .id(33L)
            .author(issuer)
            .celestialBody(CelestialBodyEntity.builder().validityTime(3).build())
            .timestamp(Instant.ofEpochSecond(1355314332L))
            .build();
    var dto = UpdateObservationDto.builder().description(JsonNullable.of("Beau Soleil")).build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(observationRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
    when(observationRepository.save(Mockito.isA(ObservationEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0));
    var observation = observationService.update(entity.getId(), dto, issuer.getUsername());

    // Then
    assertThat(observation.getDescription()).isEqualTo(dto.getDescription().get());
  }

  @DisplayName("ObservationService#update should update the targeted observation (issuer is admin)")
  @Test
  void update_should_update_the_targeted_observation_with_issuer_admin() {
    // Given
    var issuer = UserEntity.builder().username("Thomas").type(Type.ADMIN).build();
    var entity =
        ObservationEntity.builder()
            .id(33L)
            .author(UserEntity.builder().username("author").build())
            .celestialBody(CelestialBodyEntity.builder().validityTime(3).build())
            .timestamp(Instant.ofEpochSecond(1355314332L))
            .build();
    var dto = UpdateObservationDto.builder().description(JsonNullable.of("Beau Soleil")).build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(observationRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
    when(observationRepository.save(Mockito.isA(ObservationEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0));
    var observation = observationService.update(entity.getId(), dto, issuer.getUsername());

    // Then
    assertThat(observation.getDescription()).isEqualTo(dto.getDescription().get());
  }

  @DisplayName("ObservationService#update should update nothing")
  @Test
  void update_should_update_nothing() {
    // Given
    var issuer = UserEntity.builder().username("Thomas").build();
    var entity =
        ObservationEntity.builder()
            .id(33L)
            .author(issuer)
            .celestialBody(CelestialBodyEntity.builder().validityTime(3).build())
            .timestamp(Instant.ofEpochSecond(1355314332L))
            .build();
    var dto = UpdateObservationDto.builder().description(JsonNullable.undefined()).build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(observationRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
    when(observationRepository.save(Mockito.isA(ObservationEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0));
    var observation = observationService.update(entity.getId(), dto, issuer.getUsername());

    // Then
    assertThat(observation.getDescription()).isEqualTo(entity.getDescription());
  }

  @DisplayName("ObservationService#update should throw when dto is invalid")
  @Test
  void update_should_throw_when_dto_is_invalid() {
    // Given
    var dto =
        UpdateObservationDto.builder()
            .description(
                JsonNullable.of(
                    "veQAONkCUMj5dmtCy7cMqpPA8o3Gw6w7YVM9UQF7EBwcpTjn7mR2JtgVeufLDOS4t3YaDXhP3es7GlKfD6et6WJM0UPBRBezVRs15LQ2IaSZf5doyohBiugaKiauXVL5SNHLH01gWD1Vxbv4hlAE15aPirARf1XZspLq7va1eaJhAH1ivskLKdhiRA9c6cN0fJPbZHm1t0kPm9jV9YAyHvM5X2wBjt6D16ynoHeonYRnHvTG2hRzV2WUtrjwqsxRhoC4zicg9iIo4rI6XOhWgjVoCCZbuUKPxwO1jdznQwP7zprZHjMPSrv19poZVf4vUMcDrAW0aSDZ0s0tdla6n8Cad4hwtQvrk7dNfmAvLh5g4Yau85AjY7Vrh09JeZPD5ezvbRZTPvoGYSzqMKXbb4rFk4FEbeXZiz3hu1pVLtzPjNqNCfimP6Y01oErLz29rsHkDrVmabdg31799yEFXWJsUWCrEDV6UtZX2kT4bmpJq7HgHWBx4"))
            .build();

    // When
    ThrowingCallable action = () -> observationService.update(1L, dto, "issuer");

    // Then
    assertThatThrownBy(action)
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("violations", Set.of("description.size"));
  }

  @DisplayName("ObservationService#update should throw when issuer can't edit observation")
  @Test
  void update_should_throw_when_issuer_cant_edit_observation() {
    // Given
    var issuer = UserEntity.builder().username("issuer").build();
    var entity =
        ObservationEntity.builder()
            .id(33L)
            .author(UserEntity.builder().username("author").build())
            .celestialBody(CelestialBodyEntity.builder().validityTime(3).build())
            .timestamp(Instant.ofEpochSecond(1355314332L))
            .build();
    var dto = UpdateObservationDto.builder().description(JsonNullable.undefined()).build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(observationRepository.findById(entity.getId())).thenReturn(Optional.of(entity));
    ThrowingCallable action =
        () -> observationService.update(entity.getId(), dto, issuer.getUsername());

    // Then
    assertThatThrownBy(action).isInstanceOf(ObservationNotEditableException.class);
  }
}
