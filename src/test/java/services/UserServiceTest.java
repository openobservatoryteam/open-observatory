package services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

import fr.openobservatory.backend.dto.ChangePasswordDto;
import fr.openobservatory.backend.dto.CreateUserDto;
import fr.openobservatory.backend.dto.UpdateProfileDto;
import fr.openobservatory.backend.entities.CelestialBodyEntity;
import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.entities.UserEntity.Type;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.ObservationRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import fr.openobservatory.backend.services.UserService;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Spy private ModelMapper modelMapper = new ModelMapper();

  @Spy private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Mock private UserRepository userRepository;

  @Mock private ObservationRepository observationRepository;

  @InjectMocks private UserService userService;

  @BeforeEach
  void setup_modelmapper() {
    modelMapper.addConverter(
        new Converter<Instant, OffsetDateTime>() {
          @Override
          public OffsetDateTime convert(MappingContext<Instant, OffsetDateTime> context) {
            return context.getSource().atOffset(ZoneOffset.UTC);
          }
        });
  }

  // --- UserService#create

  @DisplayName("UserService#create should pass with valid DTO")
  @Test
  void create_should_pass_with_valid_dto() {
    // Given
    var dto = new CreateUserDto("heidi", "Heidi123", "This is me");

    // When
    when(userRepository.existsByUsernameIgnoreCase(dto.getUsername())).thenReturn(false);
    when(userRepository.save(isA(UserEntity.class))).then(a -> a.getArgument(0));
    var user = userService.create(dto);

    // Then
    assertThat(user.getAchievements()).isEmpty();
    assertThat(user.getAvatar()).isNull();
    assertThat(user.getBiography()).isEqualTo(dto.getBiography());
    assertThat(user.getKarma()).isEqualTo(0);
    assertThat(user.getType()).isEqualTo(Type.USER);
    assertThat(user.getUsername()).isEqualTo(dto.getUsername());
  }

  @DisplayName("UserService#create should fail with invalid username")
  @Test
  void create_should_fail_with_invalid_username() {
    // Given
    var dto = new CreateUserDto("(heidi .", "Heidi123", "This is me");

    // When
    ThrowingCallable action = () -> userService.create(dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidUsernameException.class);
  }

  @DisplayName("UserService#create should fail with existing username")
  @Test
  void create_should_fail_with_existing_username() {
    // Given
    var dto = new CreateUserDto("heidi", "Heidi123", "This is me");

    // When
    when(userRepository.existsByUsernameIgnoreCase(dto.getUsername())).thenReturn(true);
    ThrowingCallable action = () -> userService.create(dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(UsernameAlreadyUsedException.class);
  }

  // --- UserService#findByUsername

  @DisplayName("UserService#findByUsername should pass with public user and administrator issuer")
  @Test
  void find_by_username_should_pass_with_public_user_and_administrator_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              entity.setType(Type.ADMIN);
              return Optional.of(entity);
            });
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setPublic(true);
              return Optional.of(entity);
            });
    var user = userService.findByUsername(username, issuerUsername);

    // Then
    assertThat(user.getUsername()).isEqualTo(username);
  }

  @DisplayName("UserService#findByUsername should pass with public user and regular issuer")
  @Test
  void find_by_username_should_pass_with_public_user_and_regular_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              entity.setType(Type.USER);
              return Optional.of(entity);
            });
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setPublic(true);
              return Optional.of(entity);
            });
    var user = userService.findByUsername(username, issuerUsername);

    // Then
    assertThat(user.getUsername()).isEqualTo(username);
  }

  @DisplayName("UserService#findByUsername should pass with public user and anonymous issuer")
  @Test
  void find_by_username_should_pass_with_public_user_and_anonymous_issuer() {
    // Given
    var username = "lima";

    // When
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setPublic(true);
              return Optional.of(entity);
            });
    var user = userService.findByUsername(username, null);

    // Then
    assertThat(user.getUsername()).isEqualTo(username);
  }

  @DisplayName("UserService#findByUsername should pass with private user and same issuer")
  @Test
  void find_by_username_should_pass_with_user_and_same_issuer() {
    // Given
    var username = "lima";

    // When
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setPublic(false);
              entity.setType(Type.USER);
              return Optional.of(entity);
            });
    var user = userService.findByUsername(username, username);

    // Then
    assertThat(user.getUsername()).isEqualTo(username);
  }

  @DisplayName("UserService#findByUsername should pass with private user and administrator issuer")
  @Test
  void find_by_username_should_pass_with_private_user_and_administrator_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              entity.setType(Type.ADMIN);
              return Optional.of(entity);
            });
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setPublic(false);
              return Optional.of(entity);
            });
    var user = userService.findByUsername(username, issuerUsername);

    // Then
    assertThat(user.getUsername()).isEqualTo(username);
  }

  @DisplayName("UserService#findByUsername should fail with private user and regular issuer")
  @Test
  void find_by_username_should_fail_with_private_user_and_regular_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              entity.setType(Type.USER);
              return Optional.of(entity);
            });
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setPublic(false);
              return Optional.of(entity);
            });
    ThrowingCallable action = () -> userService.findByUsername(username, issuerUsername);

    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotVisibleException.class);
  }

  @DisplayName("UserService#findByUsername should fail with private user and anonymous issuer")
  @Test
  void find_by_username_should_fail_with_private_user_and_anonymous_issuer() {
    // Given
    var username = "lima";

    // When
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setPublic(false);
              return Optional.of(entity);
            });
    ThrowingCallable action = () -> userService.findByUsername(username, null);

    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotVisibleException.class);
  }

  @DisplayName("UserService#findByUsername should fail with unknown user")
  @Test
  void find_by_username_should_fail_with_unknown_user() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              entity.setType(Type.USER);
              return Optional.of(entity);
            });
    when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
    ThrowingCallable action = () -> userService.findByUsername(username, issuerUsername);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownUserException.class);
  }

  @DisplayName("UserService#findByUsername should fail with unavailable issuer")
  @Test
  void find_by_username_should_fail_with_unavailable_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername)).thenReturn(Optional.empty());
    ThrowingCallable action = () -> userService.findByUsername(username, issuerUsername);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnavailableUserException.class);
  }

  // --- UserService#findObservationByUsername
  @DisplayName(
      "UserService#findObservationByUsername should return user's observations when issuer is the targeted user with private profile")
  @Test
  void findObservationByUsername_should_return_user_observations_when_issuer_is_targeted_user() {
    // Given
    var targetedUser = "Eikjos";
    var issuer = "Eikjos";
    var celestialBody = new CelestialBodyEntity();
    celestialBody.setValidityTime(5);
    var observationId = 2L;
    var desc = "Ceci est le soleil";
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    System.out.println(createdAt);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(Type.USER);
              return Optional.of(user);
            });
    Mockito.when(userRepository.findByUsernameIgnoreCase(targetedUser))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(targetedUser);
              user.setType(Type.USER);
              user.setPublic(false);
              return Optional.of(user);
            });
    Mockito.when(
            observationRepository.findAllByAuthor(
                Mockito.isA(UserEntity.class), Mockito.isA(Pageable.class)))
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              observation.setAuthor(answer.getArgument(0));
              observation.setId(observationId);
              observation.setDescription(desc);
              observation.setCelestialBody(celestialBody);
              observation.setCreatedAt(createdAt);
              System.out.println(observation.getCreatedAt());
              var list = List.of(observation);
              return new PageImpl<>(list, Pageable.ofSize(100), 1);
            });
    var observations = userService.findObservationsByUsername(targetedUser, issuer);
    // Then
    assertThat(observations).hasSize(1);
    var observation = observations.get(0);
    System.out.println(observation);
    System.out.println(observation.getCreatedAt());
    assertThat(observation.getId()).isEqualTo(observationId);
    assertThat(observation.getDescription()).isEqualTo(desc);
    assertThat(observation.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observation.getCelestialBody().getValidityTime()).isEqualTo(5);
    assertThat(observation.getAuthor().getUsername()).isEqualTo(targetedUser);
  }

  @DisplayName(
      "UserService#findObservationByUsername should return user's observations when issuer is not the targeted user with public profile")
  @Test
  void
      findObservationByUsername_should_return_user_observations_when_issuer_is_not_target_but_profile_is_public() {
    // Given
    var targetedUser = "Eikjos";
    var issuer = "Keke";
    var celestialBody = new CelestialBodyEntity();
    celestialBody.setValidityTime(5);
    var observationId = 2L;
    var desc = "Ceci est le soleil";
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    System.out.println(createdAt);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(Type.USER);
              return Optional.of(user);
            });
    Mockito.when(userRepository.findByUsernameIgnoreCase(targetedUser))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(targetedUser);
              user.setType(Type.USER);
              user.setPublic(true);
              return Optional.of(user);
            });
    Mockito.when(
            observationRepository.findAllByAuthor(
                Mockito.isA(UserEntity.class), Mockito.isA(Pageable.class)))
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              observation.setAuthor(answer.getArgument(0));
              observation.setId(observationId);
              observation.setDescription(desc);
              observation.setCelestialBody(celestialBody);
              observation.setCreatedAt(createdAt);
              System.out.println(observation.getCreatedAt());
              var list = List.of(observation);
              return new PageImpl<>(list, Pageable.ofSize(100), 1);
            });
    var observations = userService.findObservationsByUsername(targetedUser, issuer);
    // Then
    assertThat(observations).hasSize(1);
    var observation = observations.get(0);
    System.out.println(observation);
    System.out.println(observation.getCreatedAt());
    assertThat(observation.getId()).isEqualTo(observationId);
    assertThat(observation.getDescription()).isEqualTo(desc);
    assertThat(observation.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observation.getCelestialBody().getValidityTime()).isEqualTo(5);
    assertThat(observation.getAuthor().getUsername()).isEqualTo(targetedUser);
  }

  @DisplayName(
      "UserService#findObservationByUsername should return user's observations when issuer is null with public profile")
  @Test
  void
      findObservationByUsername_should_return_user_observations_when_issuer_is_null_but_profile_is_public() {
    // Given
    var targetedUser = "Eikjos";
    String issuer = null;
    var celestialBody = new CelestialBodyEntity();
    celestialBody.setValidityTime(5);
    var observationId = 2L;
    var desc = "Ceci est le soleil";
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    System.out.println(createdAt);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(targetedUser))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(targetedUser);
              user.setType(Type.USER);
              user.setPublic(true);
              return Optional.of(user);
            });
    Mockito.when(
            observationRepository.findAllByAuthor(
                Mockito.isA(UserEntity.class), Mockito.isA(Pageable.class)))
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              observation.setAuthor(answer.getArgument(0));
              observation.setId(observationId);
              observation.setDescription(desc);
              observation.setCelestialBody(celestialBody);
              observation.setCreatedAt(createdAt);
              System.out.println(observation.getCreatedAt());
              var list = List.of(observation);
              return new PageImpl<>(list, Pageable.ofSize(100), 1);
            });
    var observations = userService.findObservationsByUsername(targetedUser, issuer);
    // Then
    assertThat(observations).hasSize(1);
    var observation = observations.get(0);
    System.out.println(observation);
    System.out.println(observation.getCreatedAt());
    assertThat(observation.getId()).isEqualTo(observationId);
    assertThat(observation.getDescription()).isEqualTo(desc);
    assertThat(observation.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observation.getCelestialBody().getValidityTime()).isEqualTo(5);
    assertThat(observation.getAuthor().getUsername()).isEqualTo(targetedUser);
  }

  @DisplayName(
      "UserService#findObservationByUsername should return user's observations when issuer is not the targeted user with private profile but issuer is admin")
  @Test
  void
      findObservationByUsername_should_return_user_observations_when_targeted_is_private_and_issuer_is_admin() {
    // Given
    var targetedUser = "Eikjos";
    var issuer = "MrAdmin";
    var celestialBody = new CelestialBodyEntity();
    celestialBody.setValidityTime(5);
    var observationId = 2L;
    var desc = "Ceci est le soleil";
    var timestamp = OffsetDateTime.of(2023, 3, 21, 18, 12, 30, 0, ZoneOffset.UTC);
    var createdAt = Instant.from(timestamp);
    System.out.println(createdAt);
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(Type.ADMIN);
              return Optional.of(user);
            });
    Mockito.when(userRepository.findByUsernameIgnoreCase(targetedUser))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(targetedUser);
              user.setType(Type.USER);
              user.setPublic(false);
              return Optional.of(user);
            });
    Mockito.when(
            observationRepository.findAllByAuthor(
                Mockito.isA(UserEntity.class), Mockito.isA(Pageable.class)))
        .thenAnswer(
            answer -> {
              var observation = new ObservationEntity();
              observation.setAuthor(answer.getArgument(0));
              observation.setId(observationId);
              observation.setDescription(desc);
              observation.setCelestialBody(celestialBody);
              observation.setCreatedAt(createdAt);
              System.out.println(observation.getCreatedAt());
              var list = List.of(observation);
              return new PageImpl<>(list, Pageable.ofSize(100), 1);
            });
    var observations = userService.findObservationsByUsername(targetedUser, issuer);
    // Then
    assertThat(observations).hasSize(1);
    var observation = observations.get(0);
    System.out.println(observation);
    System.out.println(observation.getCreatedAt());
    assertThat(observation.getId()).isEqualTo(observationId);
    assertThat(observation.getDescription()).isEqualTo(desc);
    assertThat(observation.getCreatedAt()).isEqualTo(timestamp);
    assertThat(observation.getCelestialBody().getValidityTime()).isEqualTo(5);
    assertThat(observation.getAuthor().getUsername()).isEqualTo(targetedUser);
  }

  @DisplayName("UserService#findObservationByeUsername should fail with unavailable issuer")
  @Test
  void findObservationByUsername_should_fail_with_unavailable_issuer() {
    // Given
    var issuer = "Hoshi";
    var targetedUser = "Tsuki";
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenThrow(UnavailableUserException.class);
    ThrowingCallable action = () -> userService.findObservationsByUsername(targetedUser, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnavailableUserException.class);
  }

  @DisplayName("UserService#findObservartionByUsername should fail with unknown targeted user")
  @Test
  void findObservationByUsername_should_fail_with_unknown_targeted_user() {
    // Given
    var issuer = "Kevin";
    var targetedUser = "Thomas";
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenReturn(Optional.of(new UserEntity()));
    Mockito.when(userRepository.findByUsernameIgnoreCase(targetedUser))
        .thenThrow(UnknownUserException.class);
    ThrowingCallable action = () -> userService.findObservationsByUsername(targetedUser, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownUserException.class);
  }

  @DisplayName(
      "UserService#findObservationByUsername should fail when issuer is not targeted user and profile is private")
  @Test
  void findObservationByUsername_should_fail_with_issuer_not_targeted_and_private_profile() {
    // Given
    var issuer = "Eikjos";
    var targetedUser = "ShyUser";
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(issuer))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(issuer);
              user.setType(Type.USER);
              return Optional.of(user);
            });
    Mockito.when(userRepository.findByUsernameIgnoreCase(targetedUser))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(targetedUser);
              user.setType(Type.USER);
              user.setPublic(false);
              return Optional.of(user);
            });
    ThrowingCallable action = () -> userService.findObservationsByUsername(targetedUser, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotVisibleException.class);
  }

  @DisplayName(
      "UserService#findObservationByUsername should fail when issuer is null and profile is private")
  @Test
  void findObservationByUsername_should_fail_with_issuer_null_and_private_profile() {
    // Given
    String issuer = null;
    var targetedUser = "ShyUser";
    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(targetedUser))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setUsername(targetedUser);
              user.setType(Type.USER);
              user.setPublic(false);
              return Optional.of(user);
            });
    ThrowingCallable action = () -> userService.findObservationsByUsername(targetedUser, issuer);
    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotVisibleException.class);
  }

  // --- UserService#findSelf

  @DisplayName("UserService#findSelf should pass with existing issuer")
  @Test
  void find_self_should_pass_with_existing_issuer() {
    // Given
    var issuerUsername = "heidi";

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              return Optional.of(entity);
            });
    var user = userService.findSelf(issuerUsername);

    // Then
    assertThat(user.getUsername()).isEqualTo(issuerUsername);
  }

  @DisplayName("UserService#findSelf should fail with unavailable issuer")
  @Test
  void find_self_should_pass_with_existing_username() {
    // Given
    var issuerUsername = "heidi";

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername)).thenReturn(Optional.empty());
    ThrowingCallable action = () -> userService.findSelf(issuerUsername);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnavailableUserException.class);
  }

  // UserService#modifyPassword

  @DisplayName(
      "UserService#modifyPassword should pass with existing user and valid data and administrator issuer")
  @Test
  void modify_password_should_pass_with_existing_user_and_valid_data_and_administrator_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    var targetedUserPassword = "helloWorld123!";
    var targetedUserPasswordHash = passwordEncoder.encode(targetedUserPassword);
    var dto = new ChangePasswordDto(targetedUserPassword, "newPassword123!");

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              entity.setType(Type.ADMIN);
              return Optional.of(entity);
            });
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setPassword(targetedUserPasswordHash);
              return Optional.of(entity);
            });
    ThrowingCallable action = () -> userService.modifyPassword(username, dto, issuerUsername);

    // Then
    assertThatNoException().isThrownBy(action);
  }

  @DisplayName(
      "UserService#modifyPassword should pass with existing user and valid data and same issuer")
  @Test
  void modify_password_should_pass_with_existing_user_and_valid_data_and_same_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "lima";

    var targetedUserPassword = "helloWorld123!";
    var targetedUserPasswordHash = passwordEncoder.encode(targetedUserPassword);
    var dto = new ChangePasswordDto(targetedUserPassword, "newPassword123!");

    // When
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setPassword(targetedUserPasswordHash);
              entity.setType(Type.USER);
              return Optional.of(entity);
            });
    ThrowingCallable action = () -> userService.modifyPassword(username, dto, issuerUsername);

    // Then
    assertThatNoException().isThrownBy(action);
  }

  @DisplayName(
      "UserService#modifyPassword should fail with existing user and valid data and different issuer")
  @Test
  void modify_password_should_fail_with_existing_user_and_valid_data_and_different_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    var targetedUserPassword = "helloWorld123!";
    var targetedUserPasswordHash = passwordEncoder.encode(targetedUserPassword);
    var dto = new ChangePasswordDto(targetedUserPassword, "newPassword123!");

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              entity.setType(Type.USER);
              return Optional.of(entity);
            });
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setPassword(targetedUserPasswordHash);
              return Optional.of(entity);
            });
    ThrowingCallable action = () -> userService.modifyPassword(username, dto, issuerUsername);

    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotEditableException.class);
  }

  @DisplayName("UserService#modifyPassword should fail with existing user and password mismatch")
  @Test
  void modify_password_should_fail_with_existing_user_and_password_mismatch() {
    // Given
    var username = "heidi";
    var issuerUsername = "heidi";

    var targetedUserPassword = "helloWorld123!";
    var targetedUserPasswordHash = passwordEncoder.encode("HelloWorld123!");
    var dto = new ChangePasswordDto(targetedUserPassword, "newPassword123!");

    // When
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setPassword(targetedUserPasswordHash);
              entity.setType(Type.USER);
              return Optional.of(entity);
            });
    ThrowingCallable action = () -> userService.modifyPassword(username, dto, issuerUsername);

    // Then
    assertThatThrownBy(action).isInstanceOf(PasswordMismatchException.class);
  }

  @DisplayName("UserService#modifyPassword should fail with unknown user")
  @Test
  void modify_password_should_fail_with_unknown_user() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    var targetedUserPassword = "helloWorld123!";
    var dto = new ChangePasswordDto(targetedUserPassword, "newPassword123!");

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              entity.setType(Type.USER);
              return Optional.of(entity);
            });
    when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
    ThrowingCallable action = () -> userService.modifyPassword(username, dto, issuerUsername);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownUserException.class);
  }

  @DisplayName("UserService#modifyPassword should fail with unknown issuer")
  @Test
  void modify_password_should_fail_with_unknown_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    var targetedUserPassword = "helloWorld123!";
    var dto = new ChangePasswordDto(targetedUserPassword, "newPassword123!");

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername)).thenReturn(Optional.empty());
    ThrowingCallable action = () -> userService.modifyPassword(username, dto, issuerUsername);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnavailableUserException.class);
  }

  // --- UserService#update

  @DisplayName(
      "UserService#update should pass with existing user and valid data and administrator issuer")
  @Test
  void update_should_pass_with_existing_user_and_valid_data_and_administrator_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";
    var biography = "This is my new biography";
    var avatar = "avatar";
    var radius = 20;

    var dto =
        new UpdateProfileDto(
            JsonNullable.of(biography),
            JsonNullable.of(avatar),
            JsonNullable.undefined(),
            JsonNullable.of(radius),
            JsonNullable.undefined());

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              entity.setType(Type.ADMIN);
              return Optional.of(entity);
            });
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              return Optional.of(entity);
            });
    when(userRepository.save(isA(UserEntity.class))).then(a -> a.getArgument(0));
    var user = userService.update(username, dto, issuerUsername);

    // Then
    assertThat(user.getUsername()).isEqualTo(username);
    assertThat(user.getBiography()).isEqualTo(biography);
    assertThat(user.getAvatar()).isEqualTo(avatar);
    assertThat(user.getRadius()).isEqualTo(radius);
  }

  @DisplayName("UserService#update should pass with existing user and valid data and same issuer")
  @Test
  void update_should_pass_with_existing_user_and_valid_data_and_same_issuer() {
    // Given
    var username = "heidi";
    var issuerUsername = "heidi";
    var biography = "This is my new biography";
    var avatar = "avatar";
    var radius = 30;
    var notificationEnabled = true;
    var dto =
        new UpdateProfileDto(
            JsonNullable.of(biography),
            JsonNullable.of(avatar),
            JsonNullable.undefined(),
            JsonNullable.of(radius),
            JsonNullable.of(notificationEnabled));

    // When
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setType(Type.USER);
              return Optional.of(entity);
            });
    when(userRepository.save(isA(UserEntity.class))).then(a -> a.getArgument(0));
    var user = userService.update(username, dto, issuerUsername);

    // Then
    assertThat(user.getUsername()).isEqualTo(username);
    assertThat(user.getBiography()).isEqualTo(biography);
    assertThat(user.getAvatar()).isEqualTo(avatar);
    assertThat(user.getRadius()).isEqualTo(radius);
    assertThat(user.isNotificationsEnabled()).isEqualTo(notificationEnabled);
  }

  @DisplayName("UserService#update should pass with existing user and no data and same issuer")
  @Test
  void update_should_pass_with_existing_user_and_no_data_and_same_issuer() {
    // Given
    var username = "heidi";
    var issuerUsername = "heidi";

    var dto =
        new UpdateProfileDto(
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined());

    // When
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              entity.setType(Type.USER);
              return Optional.of(entity);
            });
    when(userRepository.save(isA(UserEntity.class))).then(a -> a.getArgument(0));
    var user = userService.update(username, dto, issuerUsername);

    // Then
    assertThat(user.getUsername()).isEqualTo(username);
  }

  @DisplayName(
      "UserService#update should fail with existing user and filled data and different issuer")
  @Test
  void update_should_fail_with_existing_user_and_valid_data_and_different_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";
    var biography = "This is my new biography";
    var avatar = "avatar";

    var dto =
        new UpdateProfileDto(
            JsonNullable.of(biography),
            JsonNullable.of(avatar),
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined());

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              entity.setType(Type.USER);
              return Optional.of(entity);
            });
    when(userRepository.findByUsernameIgnoreCase(username))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(username);
              return Optional.of(entity);
            });
    ThrowingCallable action = () -> userService.update(username, dto, issuerUsername);

    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotEditableException.class);
  }

  @DisplayName("UserService#update should fail with unknown user")
  @Test
  void update_should_fail_with_unknown_user() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    var dto =
        new UpdateProfileDto(
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined());

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername))
        .then(
            a -> {
              var entity = new UserEntity();
              entity.setUsername(issuerUsername);
              return Optional.of(entity);
            });
    ThrowingCallable action = () -> userService.update(username, dto, issuerUsername);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownUserException.class);
  }

  @DisplayName("UserService#update should fail with unknown issuer")
  @Test
  void update_should_fail_with_unknown_issuer() {
    // Given
    var username = "lima";
    var issuerUsername = "heidi";

    var dto =
        new UpdateProfileDto(
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined(),
            JsonNullable.undefined());

    // When
    when(userRepository.findByUsernameIgnoreCase(issuerUsername)).thenReturn(Optional.empty());
    ThrowingCallable action = () -> userService.update(username, dto, issuerUsername);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnavailableUserException.class);
  }
}
