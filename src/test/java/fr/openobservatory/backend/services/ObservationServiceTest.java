package fr.openobservatory.backend.services;

import static org.assertj.core.api.Assertions.*;

import fr.openobservatory.backend.dto.CreateObservationDto;
import fr.openobservatory.backend.dto.UpdateObservationDto;
import fr.openobservatory.backend.dto.VoteDto;
import fr.openobservatory.backend.entities.CelestialBodyEntity;
import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.CelestialBodyRepository;
import fr.openobservatory.backend.repositories.ObservationRepository;
import fr.openobservatory.backend.repositories.ObservationVoteRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.openapitools.jackson.nullable.JsonNullable;

@ExtendWith(MockitoExtension.class)
class ObservationServiceTest {
  @Mock private CelestialBodyRepository celestialBodyRepository;
  @Spy private ModelMapper modelMapper = new ModelMapper();
  @Mock private ObservationRepository observationRepository;
  @Mock private ObservationVoteRepository observationVoteRepository;
  @Mock private UserRepository userRepository;
  @Mock private AchievementService achievementService;

  @InjectMocks private ObservationService observationService;

  @BeforeEach
  void setup_modelMapper() {
    modelMapper.addConverter(
        new Converter<Instant, OffsetDateTime>() {
          @Override
          public OffsetDateTime convert(MappingContext<Instant, OffsetDateTime> context) {
            return context.getSource().atOffset(ZoneOffset.UTC);
          }
        });
  }

  @DisplayName("ObservationService#should return registered observation with null description")
  @Test
  void create_should_return_registered_observation_with_nul_desc() {
    // Given
    var username = "Keke27210";
    var celestialBodyId = 2L;
    String desc = null;
    var lng = 49.3;
    var lat = 12.5;
    var orientation = 30;
    var visibility = ObservationEntity.Visibility.VISIBLE;
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createDto =
        new CreateObservationDto(
            celestialBodyId, desc, lng, lat, orientation, visibility, timestamp);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(username))
        .thenAnswer(
            answer -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              return Optional.of(entity);
            });
    Mockito.when(celestialBodyRepository.findById(celestialBodyId))
        .thenAnswer(
            answer -> {
              var entity = new CelestialBodyEntity();
              entity.setId(celestialBodyId);
              return Optional.of(entity);
            });
    Mockito.when(observationRepository.save(Mockito.isA(ObservationEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0));
    var observation = observationService.create(username, createDto);
    // Then
    assertThat(observation.getAuthor().getUsername()).isEqualTo(username);
    assertThat(observation.getCelestialBody().getId()).isEqualTo(celestialBodyId);
    assertThat(observation.getDescription()).isEqualTo(desc);
    assertThat(observation.getLongitude()).isEqualTo(lng);
    assertThat(observation.getLatitude()).isEqualTo(lat);
    assertThat(observation.getOrientation()).isEqualTo(orientation);
    assertThat(observation.getVisibility()).isEqualTo(visibility);
    assertThat(observation.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observation.getCurrentVote()).isNull();
    assertThat(observation.isExpired()).isFalse();
    assertThat(observation.getKarma()).isZero();
  }

  @DisplayName("ObservationService#should return registered observation")
  @Test
  void create_should_return_registered_observation() {
    // Given
    var username = "Keke27210";
    var celestialBodyId = 2L;
    var desc = "Ma lune";
    var lng = 49.3;
    var lat = 12.5;
    var orientation = 30;
    var visibility = ObservationEntity.Visibility.VISIBLE;
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createDto =
        new CreateObservationDto(
            celestialBodyId, desc, lng, lat, orientation, visibility, timestamp);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(username))
        .thenAnswer(
            answer -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              return Optional.of(entity);
            });
    Mockito.when(celestialBodyRepository.findById(celestialBodyId))
        .thenAnswer(
            answer -> {
              var entity = new CelestialBodyEntity();
              entity.setId(celestialBodyId);
              return Optional.of(entity);
            });
    Mockito.when(observationRepository.save(Mockito.isA(ObservationEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0));
    var observation = observationService.create(username, createDto);
    // Then
    assertThat(observation.getAuthor().getUsername()).isEqualTo(username);
    assertThat(observation.getCelestialBody().getId()).isEqualTo(celestialBodyId);
    assertThat(observation.getDescription()).isEqualTo(desc);
    assertThat(observation.getLongitude()).isEqualTo(lng);
    assertThat(observation.getLatitude()).isEqualTo(lat);
    assertThat(observation.getOrientation()).isEqualTo(orientation);
    assertThat(observation.getVisibility()).isEqualTo(visibility);
    assertThat(observation.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observation.getCurrentVote()).isNull();
    assertThat(observation.isExpired()).isFalse();
    assertThat(observation.getKarma()).isZero();
  }

  @DisplayName("ObservationService#create should fail with too long description")
  @Test
  void create_should_fail_with_too_long_description() {
    // Given
    var username = "Keke27210";
    var celestialBodyId = 2L;
    var lng = 49.3;
    var lat = 12.5;
    var orientation = 30;
    var visibility = ObservationEntity.Visibility.VISIBLE;
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var description =
        "La Terre est la troisième planète par ordre d'éloignement au Soleil et la cinquième plus grande du Système solaire aussi bien par la masse que par le diamètre. Par ailleurs, elle est le seul objet céleste connu pour abriter la vie. Elle orbite autour du Soleil en 365,256 jours solaires — une année sidérale — et réalise une rotation sur elle-même relativement au Soleil en un jour sidéral (environ 23 h 56 min 4 s), soit un peu moins que son jour solaire de 24 h du fait de ce déplacement autour du Soleila. L'axe de rotation de la Terre possède une inclinaison de 23°, ce qui cause l'apparition des saisons.\nD'après la datation radiométrique, la Terre s'est formée il y a 4,54 milliards d'années. Elle possède un unique satellite naturel, la Lune, qui s'est formée peu après. L'interaction gravitationnelle avec son satellite crée les marées, stabilise son axe de rotation et réduit graduellement sa vitesse de rotation. La vie serait apparue dans les océans il y a au moins 3,5 milliards d'années, ce qui a affecté l'atmosphère et la surface terrestre par la prolifération d'organismes d'abord anaérobies puis, à la suite de l'explosion cambrienne, aérobies. Une combinaison de facteurs tels que la distance de la Terre au Soleil (environ 150 millions de kilomètres — une unité astronomique), son atmosphère, sa couche d'ozone, son champ magnétique et son évolution géologique ont permis à la vie d'évoluer et de se développer. Durant l'histoire évolutive du vivant, la biodiversité a connu de longues périodes d'expansion occasionnellement ponctuées par des extinctions massives ; environ 99 % des espèces qui ont un jour vécu sur Terre sont maintenant éteintes. En 2022, plus de 7,9 milliards d'êtres humains vivent sur Terre et dépendent de sa biosphère et de ses ressources naturelles pour leur survie.\nLa Terre est la planète la plus dense du Système solaire ainsi que la plus grande et massive des quatre planètes telluriques. Son enveloppe rigide — appelée la lithosphère — est divisée en différentes plaques tectoniques qui migrent de quelques centimètres par an. Environ 71 % de la surface de la planète est couverte d'eau — notamment des océans, mais aussi des lacs et rivières, constituant l'hydrosphère — et les 29 % restants sont des continents et des îles. La majeure partie des régions polaires est couverte de glace, notamment avec l'inlandsis de l'Antarctique et la banquise de l'océan Arctique. La structure interne de la Terre est géologiquement active, le noyau interne solide et le noyau externe liquide (composés tous deux essentiellement de fer) permettant notamment de générer le champ magnétique terrestre par effet dynamo et la convection du manteau terrestre (composé de roches silicatées) étant la cause de la tectonique des plaques.";
    var createDto =
        new CreateObservationDto(
            celestialBodyId, description, lng, lat, orientation, visibility, timestamp);
    // When
    ThrowableAssert.ThrowingCallable action = () -> observationService.create(username, createDto);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidObservationDescriptionException.class);
  }

  @DisplayName("ObservationService#create should fail with unknown issuer ")
  @Test
  void create_should_fail_with_unknown_issuer() {
    // Given
    var issuer = "Keke27210";
    var celestialBodyId = 2L;
    var desc = "La lune";
    var lng = 49.3;
    var lat = 12.5;
    var orientation = 30;
    var visibility = ObservationEntity.Visibility.VISIBLE;
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createDto =
        new CreateObservationDto(
            celestialBodyId, desc, lng, lat, orientation, visibility, timestamp);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenThrow(UnavailableUserException.class);
    ThrowableAssert.ThrowingCallable action = () -> observationService.create(issuer, createDto);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnavailableUserException.class);
  }

  @DisplayName("Observation Service should fail with unknown celestial body")
  @Test
  void create_should_fail_with_unknown_celestialBody() {
    // Given
    var issuer = "Keke27210";
    var celestialBodyId = 2L;
    var desc = "La lune";
    var lng = 49.3;
    var lat = 12.5;
    var orientation = 30;
    var visibility = ObservationEntity.Visibility.VISIBLE;
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createDto =
        new CreateObservationDto(
            celestialBodyId, desc, lng, lat, orientation, visibility, timestamp);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenReturn(Optional.of(new UserEntity()));
    Mockito.when(celestialBodyRepository.findById(celestialBodyId))
        .thenThrow(InvalidCelestialBodyIdException.class);
    ThrowableAssert.ThrowingCallable action = () -> observationService.create(issuer, createDto);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidCelestialBodyIdException.class);
  }

  @DisplayName("ObservationService#findById should return observation with given id")
  @Test
  void findById_should_return_observation_with_given_id() {
    // Given
    var observationId = 2L;
    var issuer = "Keke27210";
    var celestialBodyId = 2L;
    var desc = "La lune";
    var lng = 49.3;
    var lat = 12.5;
    var orientation = 30;
    var visibility = ObservationEntity.Visibility.VISIBLE;
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    var celestialBodyName = "Lune";
    var celestialBodyImage = "celestialBodyImage";
    var celestialBodyValidityTime = 5;
    var celestialBody = new CelestialBodyEntity();
    celestialBody.setId(celestialBodyId);
    celestialBody.setName(celestialBodyName);
    celestialBody.setImage(celestialBodyImage);
    celestialBody.setValidityTime(celestialBodyValidityTime);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              return Optional.of(user);
            });
    Mockito.when(observationRepository.findById(observationId))
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              observation.setId(observationId);
              observation.setDescription(desc);
              observation.setLatitude(lat);
              observation.setLongitude(lng);
              observation.setOrientation(orientation);
              observation.setVisibility(visibility);
              observation.setCreatedAt(createdAt);
              observation.setCelestialBody(celestialBody);
              return Optional.of(observation);
            });
    Mockito.when(
            observationVoteRepository.findAllByObservation(Mockito.isA(ObservationEntity.class)))
        .thenAnswer(
            answer -> {
              var voteEntity = new ObservationVoteEntity();
              voteEntity.setObservation(answer.getArgument(0));
              voteEntity.setVote(ObservationVoteEntity.VoteType.UPVOTE);
              return Set.of(voteEntity);
            });
    Mockito.when(
            observationVoteRepository.findByObservationAndUser(
                Mockito.isA(ObservationEntity.class), Mockito.isA(UserEntity.class)))
        .thenAnswer(
            answer -> {
              var voteEntity = new ObservationVoteEntity();
              voteEntity.setObservation(answer.getArgument(0));
              voteEntity.setUser(answer.getArgument(1));
              voteEntity.setVote(ObservationVoteEntity.VoteType.UPVOTE);
              return Optional.of(voteEntity);
            });
    var observationWithDetails = observationService.findById(observationId, issuer);
    // Then
    assertThat(observationWithDetails.getId()).isEqualTo(observationId);
    assertThat(observationWithDetails.getDescription()).isEqualTo(desc);
    assertThat(observationWithDetails.getLatitude()).isEqualTo(lat);
    assertThat(observationWithDetails.getLongitude()).isEqualTo(lng);
    assertThat(observationWithDetails.getVisibility()).isEqualTo(visibility);
    assertThat(observationWithDetails.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observationWithDetails.getCelestialBody().getId()).isEqualTo(celestialBodyId);
    assertThat(observationWithDetails.getCelestialBody().getName()).isEqualTo(celestialBodyName);
    assertThat(observationWithDetails.getCelestialBody().getImage()).isEqualTo(celestialBodyImage);
    assertThat(observationWithDetails.getCelestialBody().getValidityTime())
        .isEqualTo(celestialBodyValidityTime);
    assertThat(observationWithDetails.getCurrentVote())
        .isEqualTo(ObservationVoteEntity.VoteType.UPVOTE);
    assertThat(observationWithDetails.isExpired()).isTrue();
    assertThat(observationWithDetails.getKarma()).isEqualTo(1);
  }

  @DisplayName("ObservationService#findById should fail with unknown user")
  @Test
  void findById_should_fail_with_unknown_user() {
    // Given
    var observationId = 2L;
    var issuer = "Keke27210";
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenThrow(UnavailableUserException.class);
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.findById(observationId, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnavailableUserException.class);
  }

  @DisplayName("ObservationService#findById should fail with unknown observation")
  @Test
  void findById_should_fail_with_unknown_observation() {
    // Given
    var observationId = 3L;
    var issuer = "EikjosTv";
    //
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              return Optional.of(user);
            });
    Mockito.when(observationRepository.findById(observationId))
        .thenThrow(UnknownObservationException.class);
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.findById(observationId, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownObservationException.class);
  }

  @DisplayName(
      "ObservationService#findById should return observation with null current vote when issuer is null")
  @Test
  void findById_should_return_observation_with_null_current_vote_with_null_issuer() {
    // Given
    var observationId = 2L;
    String issuer = null;
    var celestialBodyId = 2L;
    var desc = "La lune";
    var lng = 49.3;
    var lat = 12.5;
    var orientation = 30;
    var visibility = ObservationEntity.Visibility.VISIBLE;
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    var celestialBodyName = "Lune";
    var celestialBodyImage = "celestialBodyImage";
    var celestialBodyValidityTime = 5;
    var celestialBody = new CelestialBodyEntity();
    celestialBody.setId(celestialBodyId);
    celestialBody.setName(celestialBodyName);
    celestialBody.setImage(celestialBodyImage);
    celestialBody.setValidityTime(celestialBodyValidityTime);
    // When
    Mockito.when(observationRepository.findById(observationId))
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              observation.setId(observationId);
              observation.setDescription(desc);
              observation.setLatitude(lat);
              observation.setLongitude(lng);
              observation.setOrientation(orientation);
              observation.setVisibility(visibility);
              observation.setCreatedAt(createdAt);
              observation.setCelestialBody(celestialBody);
              return Optional.of(observation);
            });
    Mockito.when(
            observationVoteRepository.findAllByObservation(Mockito.isA(ObservationEntity.class)))
        .thenAnswer(
            answer -> {
              var voteEntity = new ObservationVoteEntity();
              voteEntity.setObservation(answer.getArgument(0));
              voteEntity.setVote(ObservationVoteEntity.VoteType.UPVOTE);
              return Set.of(voteEntity);
            });
    var observationWithDetails = observationService.findById(observationId, issuer);
    // Then
    assertThat(observationWithDetails.getId()).isEqualTo(observationId);
    assertThat(observationWithDetails.getDescription()).isEqualTo(desc);
    assertThat(observationWithDetails.getLatitude()).isEqualTo(lat);
    assertThat(observationWithDetails.getLongitude()).isEqualTo(lng);
    assertThat(observationWithDetails.getVisibility()).isEqualTo(visibility);
    assertThat(observationWithDetails.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observationWithDetails.getCelestialBody().getId()).isEqualTo(celestialBodyId);
    assertThat(observationWithDetails.getCelestialBody().getName()).isEqualTo(celestialBodyName);
    assertThat(observationWithDetails.getCelestialBody().getImage()).isEqualTo(celestialBodyImage);
    assertThat(observationWithDetails.getCelestialBody().getValidityTime())
        .isEqualTo(celestialBodyValidityTime);
    assertThat(observationWithDetails.getCurrentVote()).isNull();
    assertThat(observationWithDetails.isExpired()).isTrue();
    assertThat(observationWithDetails.getKarma()).isEqualTo(1);
  }

  @DisplayName("ObservationService#findAllNearby")
  @Test
  void findAllNearby_should_return_observations_near_to_given_point() {
    // Given
    var lng = 42.12;
    var lat = 30.2;
    var radius = 30.0;
    var observationId = 2L;
    var celestialBodyId = 2L;
    var desc = "La lune";
    var orientation = 30;
    var visibility = ObservationEntity.Visibility.VISIBLE;
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    var celestialBodyName = "Lune";
    var celestialBodyImage = "celestialBodyImage";
    var celestialBodyValidityTime = 5;
    var celestialBody = new CelestialBodyEntity();
    celestialBody.setId(celestialBodyId);
    celestialBody.setName(celestialBodyName);
    celestialBody.setImage(celestialBodyImage);
    celestialBody.setValidityTime(celestialBodyValidityTime);
    var username = "EikjosTV";
    var user = new UserEntity();
    user.setUsername(username);
    // When
    Mockito.when(
            observationRepository.findAllNearby(
                Mockito.isA(Double.class),
                Mockito.isA(Double.class),
                Mockito.isA(Double.class),
                Mockito.isA(Double.class)))
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              observation.setId(observationId);
              observation.setDescription(desc);
              observation.setLatitude(lat);
              observation.setLongitude(lng);
              observation.setOrientation(orientation);
              observation.setCelestialBody(celestialBody);
              observation.setAuthor(user);
              observation.setVisibility(visibility);
              observation.setCreatedAt(createdAt);
              return List.of(observation);
            });
    var observations = observationService.findAllNearby(lng, lat, radius);
    // Then
    assertThat(observations).hasSize(1);
    var observation = observations.get(0);
    assertThat(observation.getId()).isEqualTo(observationId);
    assertThat(observation.getDescription()).isEqualTo(desc);
    assertThat(observation.getLatitude()).isEqualTo(lat);
    assertThat(observation.getLongitude()).isEqualTo(lng);
    assertThat(observation.getOrientation()).isEqualTo(orientation);
    assertThat(observation.getVisibility()).isEqualTo(visibility);
    assertThat(observation.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observation.getCelestialBody().getId()).isEqualTo(celestialBodyId);
    assertThat(observation.getCelestialBody().getName()).isEqualTo(celestialBodyName);
    assertThat(observation.getCelestialBody().getImage()).isEqualTo(celestialBodyImage);
    assertThat(observation.getCelestialBody().getValidityTime())
        .isEqualTo(celestialBodyValidityTime);
    assertThat(observation.getAuthor().getUsername()).isEqualTo(username);
  }

  @DisplayName("ObservationService#search should return list of observation")
  @Test
  void search_should_return_list_of_observation() {
    // Given
    var itemsPerPage = 2;
    var page = 1;
    var id = 1L;
    var desc = "Belle étoile";
    var lat = 45.2;
    var lng = 35.2;
    var orientation = 200;
    var visibility = ObservationEntity.Visibility.VISIBLE;
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    // When
    Mockito.when(observationRepository.findAll())
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              observation.setId(id);
              observation.setDescription(desc);
              observation.setLatitude(lat);
              observation.setLongitude(lng);
              observation.setOrientation(orientation);
              observation.setVisibility(visibility);
              observation.setCreatedAt(createdAt);
              return List.of(observation);
            });
    var observations = observationService.search(page, itemsPerPage);
    // Then
    assertThat(observations).hasSize(1);
    var observation = observations.get(0);
    assertThat(observation.getId()).isEqualTo(id);
    assertThat(observation.getDescription()).isEqualTo(desc);
    assertThat(observation.getLatitude()).isEqualTo(lat);
    assertThat(observation.getLongitude()).isEqualTo(lng);
    assertThat(observation.getOrientation()).isEqualTo(orientation);
    assertThat(observation.getVisibility()).isEqualTo(visibility);
    assertThat(observation.getCreatedAt()).isEqualTo(timestamp);
  }

  @DisplayName("ObservationService#search should fail with below zero items per page")
  @Test
  void search_should_fail_with_below_zero_items_per_page() {
    // Given
    var itemsPerPage = -1;
    var page = 1;
    // When
    ThrowableAssert.ThrowingCallable action = () -> observationService.search(page, itemsPerPage);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidPaginationException.class);
  }

  @DisplayName("ObservationService#search should fail with too high zero items per page")
  @Test
  void search_should_fail_with_too_high_items_per_page() {
    // Given
    var itemsPerPage = 200;
    var page = 1;
    // When
    ThrowableAssert.ThrowingCallable action = () -> observationService.search(page, itemsPerPage);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidPaginationException.class);
  }

  @DisplayName("ObservationService#search should fail with too high zero items per page")
  @Test
  void search_should_fail_with_below_zero_page_number() {
    // Given
    var itemsPerPage = 50;
    var page = -1;
    // When
    ThrowableAssert.ThrowingCallable action = () -> observationService.search(page, itemsPerPage);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidPaginationException.class);
  }

  @DisplayName("ObservationService#submitVote should not fail with valid arguments")
  @Test
  void submitVote_should_not_fail_with_valid_argument() {
    // Given
    var observationId = 1L;
    var issuer = "EikjosTV";
    var voteDto = new VoteDto();
    voteDto.setVote(ObservationVoteEntity.VoteType.UPVOTE);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              return Optional.of(user);
            });
    Mockito.when(observationRepository.findById(observationId))
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              return Optional.of(observation);
            });
    Mockito.when(
            observationVoteRepository.findByObservationAndUser(
                Mockito.isA(ObservationEntity.class), Mockito.isA(UserEntity.class)))
        .thenReturn(Optional.empty());
    Mockito.when(observationVoteRepository.save(Mockito.isA(ObservationVoteEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0));
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.submitVote(observationId, voteDto, issuer);
    // Then
    assertThatNoException().isThrownBy(action);
  }

  @DisplayName("ObservationService#submitVote should not fail with null vote and no current vote")
  @Test
  void submitVote_should_not_fail_with_null_vote_and_no_current_vote() {
    // Given
    var observationId = 1L;
    var issuer = "EikjosTV";
    var voteDto = new VoteDto();
    voteDto.setVote(null);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              return Optional.of(user);
            });
    Mockito.when(observationRepository.findById(observationId))
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              return Optional.of(observation);
            });
    Mockito.when(
            observationVoteRepository.findByObservationAndUser(
                Mockito.isA(ObservationEntity.class), Mockito.isA(UserEntity.class)))
        .thenReturn(Optional.empty());
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.submitVote(observationId, voteDto, issuer);
    // Then
    assertThatNoException().isThrownBy(action);
  }

  @DisplayName("ObservationService#submitVote should not fail with null vote and current vote set")
  @Test
  void submitVote_should_not_fail_with_null_vote_and_current_vote_set() {
    // Given
    var observationId = 1L;
    var issuer = "EikjosTV";
    var voteDto = new VoteDto();
    voteDto.setVote(null);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              return Optional.of(user);
            });
    Mockito.when(observationRepository.findById(observationId))
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              return Optional.of(observation);
            });
    Mockito.when(
            observationVoteRepository.findByObservationAndUser(
                Mockito.isA(ObservationEntity.class), Mockito.isA(UserEntity.class)))
        .thenAnswer(
            answer -> {
              var vote = new ObservationVoteEntity();
              vote.setVote(ObservationVoteEntity.VoteType.DOWNVOTE);
              return Optional.of(vote);
            });
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.submitVote(observationId, voteDto, issuer);
    // Then
    assertThatNoException().isThrownBy(action);
  }

  @DisplayName("ObservationService#submitVote should not fail with null vote and current vote set")
  @Test
  void submitVote_should_not_fail_with_valid_vote_and_current_vote_set() {
    // Given
    var observationId = 1L;
    var issuer = "EikjosTV";
    var voteDto = new VoteDto();
    voteDto.setVote(ObservationVoteEntity.VoteType.UPVOTE);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              return Optional.of(user);
            });
    Mockito.when(observationRepository.findById(observationId))
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              return Optional.of(observation);
            });
    Mockito.when(
            observationVoteRepository.findByObservationAndUser(
                Mockito.isA(ObservationEntity.class), Mockito.isA(UserEntity.class)))
        .thenAnswer(
            answer -> {
              var vote = new ObservationVoteEntity();
              vote.setVote(ObservationVoteEntity.VoteType.DOWNVOTE);
              return Optional.of(vote);
            });
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.submitVote(observationId, voteDto, issuer);
    // Then
    assertThatNoException().isThrownBy(action);
  }

  @DisplayName("ObservationService#submitVote should fail with unknown user")
  @Test
  void submitVote_should_fail_with_unknown_user() {
    // Given
    var issuer = "BiojoKev";
    var observationId = 2L;
    var voteDto = new VoteDto();
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenThrow(UnavailableUserException.class);
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.submitVote(observationId, voteDto, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnavailableUserException.class);
  }

  @DisplayName("ObservationService#submitVote should fail with unknown observation")
  @Test
  void submitVote_should_fail_with_unknown_observation() {
    // Given
    var issuer = "KevinBiojout";
    var observationId = 1000L;
    var voteDto = new VoteDto();
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(answer -> Optional.of(new UserEntity()));
    Mockito.when(observationRepository.findById(observationId))
        .thenThrow(UnknownObservationException.class);
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.submitVote(observationId, voteDto, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownObservationException.class);
  }

  @DisplayName(
      "ObservationService#update should return updated observation with valid arguments when issuer is the observation author")
  @Test
  void update_should_return_updated_observation_with_valid_argument_when_issuer_is_author() {
    // Given
    var issuer = "Thomas";
    var observationId = 5L;
    var desc = "Jolie Lune";
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    var newDesc = JsonNullable.of("Beau Soleil");
    var updateDto = new UpdateObservationDto();
    updateDto.setDescription(newDesc);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(UserEntity.Type.USER);
              return Optional.of(user);
            });
    Mockito.when(observationRepository.findById(observationId))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(UserEntity.Type.USER);
              var observation = new ObservationEntity();
              observation.setId(observationId);
              observation.setDescription(desc);
              observation.setAuthor(user);
              observation.setCreatedAt(createdAt);
              return Optional.of(observation);
            });
    Mockito.when(observationRepository.save(Mockito.isA(ObservationEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0));
    var observationWithDetails = observationService.update(observationId, updateDto, issuer);
    // Then
    assertThat(observationWithDetails.getId()).isEqualTo(observationId);
    assertThat(observationWithDetails.getDescription()).isEqualTo(newDesc.get());
    assertThat(observationWithDetails.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observationWithDetails.getAuthor().getUsername()).isEqualTo(issuer);
  }

  @DisplayName(
      "ObservationService#update should return updated observation with valid arguments when issuer is the observation author and description is not present")
  @Test
  void
      update_should_return_updated_observation_with_valid_argument_when_issuer_is_author_and_description_is_not_present() {
    // Given
    var issuer = "Thomas";
    var observationId = 5L;
    var desc = "Jolie Lune";
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    JsonNullable<String> newDesc = JsonNullable.undefined();
    var updateDto = new UpdateObservationDto();
    updateDto.setDescription(newDesc);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(UserEntity.Type.USER);
              return Optional.of(user);
            });
    Mockito.when(observationRepository.findById(observationId))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(UserEntity.Type.USER);
              var observation = new ObservationEntity();
              observation.setId(observationId);
              observation.setDescription(desc);
              observation.setAuthor(user);
              observation.setCreatedAt(createdAt);
              return Optional.of(observation);
            });
    Mockito.when(observationRepository.save(Mockito.isA(ObservationEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0));
    var observationWithDetails = observationService.update(observationId, updateDto, issuer);
    // Then
    assertThat(observationWithDetails.getId()).isEqualTo(observationId);
    assertThat(observationWithDetails.getDescription()).isEqualTo(desc);
    assertThat(observationWithDetails.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observationWithDetails.getAuthor().getUsername()).isEqualTo(issuer);
  }

  @DisplayName(
      "ObservationService#update should return updated observation with valid arguments when issuer is an admin")
  @Test
  void update_should_return_updated_observation_with_valid_argument_when_issuer_is_admin() {
    // Given
    var issuer = "Thomas";
    var author = "Kevin";
    var observationId = 5L;
    var desc = "Jolie Lune";
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    var newDesc = JsonNullable.of("Beau Soleil");
    var updateDto = new UpdateObservationDto();
    updateDto.setDescription(newDesc);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(UserEntity.Type.ADMIN);
              return Optional.of(user);
            });
    Mockito.when(observationRepository.findById(observationId))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(author);
              user.setType(UserEntity.Type.USER);
              var observation = new ObservationEntity();
              observation.setId(observationId);
              observation.setDescription(desc);
              observation.setAuthor(user);
              observation.setCreatedAt(createdAt);
              return Optional.of(observation);
            });
    Mockito.when(observationRepository.save(Mockito.isA(ObservationEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0));
    var observationWithDetails = observationService.update(observationId, updateDto, issuer);
    // Then
    assertThat(observationWithDetails.getId()).isEqualTo(observationId);
    assertThat(observationWithDetails.getDescription()).isEqualTo(newDesc.get());
    assertThat(observationWithDetails.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observationWithDetails.getAuthor().getUsername()).isEqualTo(author);
  }

  @DisplayName("ObservationService#update should fail with unknown user")
  @Test
  void update_should_fail_with_unknown_user() {
    // Given
    var issuer = "Romain";
    var observationId = 4L;
    var updateDto = new UpdateObservationDto();
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenThrow(UnavailableUserException.class);
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.update(observationId, updateDto, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnavailableUserException.class);
  }

  @DisplayName("ObservationService#update should fail with unknown observation")
  @Test
  void update_should_fail_with_unknown_observation() {
    // Given
    var issuer = "IronMan";
    var observationId = 479L;
    var updateDto = new UpdateObservationDto();
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(answer -> Optional.of(new UserEntity()));
    Mockito.when(observationRepository.findById(observationId))
        .thenThrow(UnknownObservationException.class);
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.update(observationId, updateDto, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownObservationException.class);
  }

  @DisplayName(
      "ObservationService#update should fail when issuer is not the observation author and not admin")
  @Test
  void update_should_fail_when_issuer_is_not_author_or_admin() {
    // Given
    var issuer = "xXNormalUserXx";
    var author = "Hulk";
    var observationId = 45L;
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    var updateDto = new UpdateObservationDto();
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(UserEntity.Type.USER);
              return Optional.of(user);
            });
    Mockito.when(observationRepository.findById(observationId))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(author);
              user.setType(UserEntity.Type.USER);
              var observation = new ObservationEntity();
              observation.setAuthor(user);
              observation.setCreatedAt(createdAt);
              return Optional.of(observation);
            });
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.update(observationId, updateDto, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(ObservationNotEditableException.class);
  }

  @DisplayName("ObservationService#update should fial with too long description")
  @Test
  void update_should_fail_with_too_long_description() {
    // Given
    var issuer = "NoBrain0_0";
    var observationId = 69L;
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    var newDescription =
        JsonNullable.of(
            "La Terre est la troisième planète par ordre d'éloignement au Soleil et la cinquième plus grande du Système solaire aussi bien par la masse que par le diamètre. Par ailleurs, elle est le seul objet céleste connu pour abriter la vie. Elle orbite autour du Soleil en 365,256 jours solaires — une année sidérale — et réalise une rotation sur elle-même relativement au Soleil en un jour sidéral (environ 23 h 56 min 4 s), soit un peu moins que son jour solaire de 24 h du fait de ce déplacement autour du Soleila. L'axe de rotation de la Terre possède une inclinaison de 23°, ce qui cause l'apparition des saisons.\nD'après la datation radiométrique, la Terre s'est formée il y a 4,54 milliards d'années. Elle possède un unique satellite naturel, la Lune, qui s'est formée peu après. L'interaction gravitationnelle avec son satellite crée les marées, stabilise son axe de rotation et réduit graduellement sa vitesse de rotation. La vie serait apparue dans les océans il y a au moins 3,5 milliards d'années, ce qui a affecté l'atmosphère et la surface terrestre par la prolifération d'organismes d'abord anaérobies puis, à la suite de l'explosion cambrienne, aérobies. Une combinaison de facteurs tels que la distance de la Terre au Soleil (environ 150 millions de kilomètres — une unité astronomique), son atmosphère, sa couche d'ozone, son champ magnétique et son évolution géologique ont permis à la vie d'évoluer et de se développer. Durant l'histoire évolutive du vivant, la biodiversité a connu de longues périodes d'expansion occasionnellement ponctuées par des extinctions massives ; environ 99 % des espèces qui ont un jour vécu sur Terre sont maintenant éteintes. En 2022, plus de 7,9 milliards d'êtres humains vivent sur Terre et dépendent de sa biosphère et de ses ressources naturelles pour leur survie.\nLa Terre est la planète la plus dense du Système solaire ainsi que la plus grande et massive des quatre planètes telluriques. Son enveloppe rigide — appelée la lithosphère — est divisée en différentes plaques tectoniques qui migrent de quelques centimètres par an. Environ 71 % de la surface de la planète est couverte d'eau — notamment des océans, mais aussi des lacs et rivières, constituant l'hydrosphère — et les 29 % restants sont des continents et des îles. La majeure partie des régions polaires est couverte de glace, notamment avec l'inlandsis de l'Antarctique et la banquise de l'océan Arctique. La structure interne de la Terre est géologiquement active, le noyau interne solide et le noyau externe liquide (composés tous deux essentiellement de fer) permettant notamment de générer le champ magnétique terrestre par effet dynamo et la convection du manteau terrestre (composé de roches silicatées) étant la cause de la tectonique des plaques.");
    var updateDto = new UpdateObservationDto();
    updateDto.setDescription(newDescription);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(UserEntity.Type.USER);
              return Optional.of(user);
            });
    Mockito.when(observationRepository.findById(observationId))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(UserEntity.Type.USER);
              var observation = new ObservationEntity();
              observation.setAuthor(user);
              observation.setCreatedAt(createdAt);
              return Optional.of(observation);
            });
    ThrowableAssert.ThrowingCallable action =
        () -> observationService.update(observationId, updateDto, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidObservationDescriptionException.class);
  }
}
