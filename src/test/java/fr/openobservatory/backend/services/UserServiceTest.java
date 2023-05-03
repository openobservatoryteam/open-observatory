package fr.openobservatory.backend.services;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import fr.openobservatory.backend.dto.input.CreateUserDto;
import fr.openobservatory.backend.dto.input.UpdatePasswordDto;
import fr.openobservatory.backend.dto.input.UpdatePositionDto;
import fr.openobservatory.backend.dto.input.UpdateUserDto;
import fr.openobservatory.backend.entities.ObservationEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity;
import fr.openobservatory.backend.entities.ObservationVoteEntity.VoteType;
import fr.openobservatory.backend.entities.UserAchievementEntity;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.entities.UserEntity.Type;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.*;
import fr.openobservatory.backend.repositories.Achievements.Achievement;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.jose4j.jwk.Use;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Spy ModelMapper modelMapper = new ModelMapper();
  @Mock ObservationRepository observationRepository;
  @Spy PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
  @Mock PushSubscriptionRepository pushSubscriptionRepository;
  @Mock UserRepository userRepository;
  @Spy Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  @InjectMocks UserService userService;

  // --- UserService#create

  @DisplayName("UserService#create should create a new user")
  @Test
  void create_should_create_a_new_user() {
    // Given
    var dto =
        CreateUserDto.builder()
            .username("Toto")
            .password("OneTwoThree123!")
            .biography("It's a me, Mario!")
            .build();

    // When
    when(observationRepository.findAllByAuthor(isA(UserEntity.class))).thenReturn(Set.of());
    when(userRepository.save(isA(UserEntity.class))).then(a -> a.getArgument(0));
    var user = userService.create(dto);

    // Then
    assertThat(user.getUsername()).isEqualTo(dto.getUsername());
    assertThat(user.getBiography()).isEqualTo(dto.getBiography());
    verify(passwordEncoder, times(1)).encode(dto.getPassword());
  }

  @DisplayName("UserService#create should throw when dto is invalid")
  @Test
  void create_should_throw_when_dto_is_invalid() {
    // Given
    var dto = CreateUserDto.builder().username("$").password("nopass").biography(null).build();

    // When
    ThrowingCallable action = () -> userService.create(dto);

    // Then
    assertThatThrownBy(action)
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("violations", Set.of("username.format", "password.size"));
  }

  @DisplayName("UserService#create should throw when username is already used")
  @Test
  void create_should_throw_when_username_is_already_used() {
    // Given
    var dto =
        CreateUserDto.builder()
            .username("Toto")
            .password("OneTwoThree123!")
            .biography("It's a me, Mario!")
            .build();

    // When
    when(userRepository.existsByUsernameIgnoreCase(dto.getUsername())).thenReturn(true);
    ThrowingCallable action = () -> userService.create(dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(UsernameAlreadyUsedException.class);
  }

  // --- UserService#delete

  @DisplayName("UserService#delete should delete the user")
  @Test
  void delete_should_delete_the_user() {
    // Given
    var issuer = UserEntity.builder()
        .username("issuer")
        .type(Type.ADMIN)
        .build();
    var target = UserEntity.builder()
        .username("target")
        .build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername())).thenReturn(Optional.of(issuer));
    when(userRepository.findByUsernameIgnoreCase(target.getUsername())).thenReturn(Optional.of(target));
    userService.delete(target.getUsername(), issuer.getUsername());

    // Then
    verify(userRepository).delete(isA(UserEntity.class));
  }

  @DisplayName("UserService#delete should throw when issuer can't delete the user")
  @Test
  void delete_should_throw_when_issuer_cant_delete_the_user() {
    // Given
    var issuer = UserEntity.builder()
        .username("issuer")
        .build();
    var target = UserEntity.builder()
        .username("target")
        .build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername())).thenReturn(Optional.of(issuer));
    when(userRepository.findByUsernameIgnoreCase(target.getUsername())).thenReturn(Optional.of(target));
    ThrowingCallable action = () -> userService.delete(target.getUsername(), issuer.getUsername());

    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotEditableException.class);
  }

  // --- UserService#findByUsername

  @DisplayName("UserService#findByUsername should find user")
  @Test
  void findByUsername_should_find_user() {
    // Given
    var target = UserEntity.builder().username("target").build();
    var issuer = UserEntity.builder().username("issuer").build();

    // When
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    var user = userService.findByUsername(target.getUsername(), issuer.getUsername());

    // Then
    assertThat(user.getUsername()).isEqualTo(target.getUsername());
  }

  @DisplayName("UserService#findByUsername should throw when profile is private")
  @Test
  void findByUsername_should_throw_when_profile_is_private() {
    // Given
    var target = UserEntity.builder().username("target").isPublic(false).build();
    var issuer = UserEntity.builder().username("issuer").build();

    // When
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    ThrowingCallable action =
        () -> userService.findByUsername(target.getUsername(), issuer.getUsername());

    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotVisibleException.class);
  }

  @DisplayName("UserService#findByUsername should throw when profile is private (no issuer)")
  @Test
  void findByUsername_should_throw_when_profile_is_private_without_issuer() {
    // Given
    var target = UserEntity.builder().username("target").isPublic(false).build();

    // When
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    ThrowingCallable action = () -> userService.findByUsername(target.getUsername(), null);

    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotVisibleException.class);
  }

  // --- UserService#findSelf

  @DisplayName("UserService#findSelf should find the issuer")
  @Test
  void findSelf_should_find_the_issuer() {
    // Given
    var issuer =
        UserEntity.builder()
            .username("issuer")
            .achievements(
                Set.of(UserAchievementEntity.builder().achievement(Achievement.JAMES_WEBB).build()))
            .build();
    var observation =
        ObservationEntity.builder()
            .votes(Set.of(ObservationVoteEntity.builder().vote(VoteType.DOWNVOTE).build()))
            .build();

    // When
    when(observationRepository.findAllByAuthor(issuer)).thenReturn(Set.of(observation));
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    var user = userService.findSelf(issuer.getUsername());

    // Then
    assertThat(user.getUsername()).isEqualTo(issuer.getUsername());
    assertThat(user.getAchievements()).isNotEmpty();
    assertThat(user.getAchievements().get(0).getAchievement()).isEqualTo(Achievement.JAMES_WEBB);
    assertThat(user.getKarma()).isEqualTo(-1);
  }

  // --- UserService#update

  @DisplayName("UserService#update should update the targeted user")
  @Test
  void update_should_update_the_targeted_user() {
    // Given
    var target =
        UserEntity.builder()
            .username("target")
            .avatar("avatar")
            .biography("Hello World")
            .isPublic(false)
            .notificationEnabled(false)
            .notificationRadius(10)
            .build();
    var issuer = UserEntity.builder().username("issuer").type(Type.ADMIN).build();
    var dto =
        UpdateUserDto.builder()
            .avatar(JsonNullable.of("new_avatar"))
            .biography(JsonNullable.of("Goodbye World!"))
            .isPublic(JsonNullable.of(true))
            .notificationEnabled(JsonNullable.of(true))
            .notificationRadius(JsonNullable.of(15))
            .build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    when(userRepository.save(isA(UserEntity.class))).then(a -> a.getArgument(0));
    var user = userService.update(target.getUsername(), dto, issuer.getUsername());

    // Then
    assertThat(user.getAvatar()).isEqualTo(dto.getAvatar().get());
    assertThat(user.getBiography()).isEqualTo(dto.getBiography().get());
  }

  @DisplayName("UserService#update should clear current subscriptions")
  @Test
  void update_should_clear_current_subscriptions() {
    // Given
    var target =
        UserEntity.builder().username("target").isPublic(false).notificationEnabled(true).build();
    var issuer = UserEntity.builder().username("issuer").type(Type.ADMIN).build();
    var dto = UpdateUserDto.builder().notificationEnabled(JsonNullable.of(false)).build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    when(userRepository.save(isA(UserEntity.class))).then(a -> a.getArgument(0));
    userService.update(target.getUsername(), dto, issuer.getUsername());

    // Then
    verify(pushSubscriptionRepository, times(1)).deleteAllByUser(target);
  }

  @DisplayName("UserService#update should update nothing")
  @Test
  void update_should_update_nothing() {
    // Given
    var target =
        UserEntity.builder()
            .username("target")
            .avatar("avatar")
            .biography("Hello World")
            .isPublic(false)
            .notificationEnabled(false)
            .notificationRadius(10)
            .build();
    var issuer = UserEntity.builder().username("issuer").type(Type.ADMIN).build();
    var dto = UpdateUserDto.builder().build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    when(userRepository.save(isA(UserEntity.class))).then(a -> a.getArgument(0));
    var user = userService.update(target.getUsername(), dto, issuer.getUsername());

    // Then
    assertThat(user.getAvatar()).isEqualTo(target.getAvatar());
    assertThat(user.getBiography()).isEqualTo(target.getBiography());
  }

  @DisplayName("UserService#update should throw when dto is invalid")
  @Test
  void update_should_throw_when_dto_is_invalid() {
    // Given
    var dto =
        UpdateUserDto.builder()
            .notificationEnabled(JsonNullable.of(true))
            .notificationRadius(JsonNullable.of(150))
            .build();

    // When
    ThrowingCallable action = () -> userService.update("target", dto, "target");

    // Then
    assertThatThrownBy(action)
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("violations", Set.of("notificationRadius.range"));
  }

  @DisplayName("UserService#update should throw when issuer can't edit target")
  @Test
  void update_should_throw_when_issuer_cant_edit_target() {
    // Given
    var target = UserEntity.builder().username("target").build();
    var issuer = UserEntity.builder().username("issuer").build();
    var dto =
        UpdateUserDto.builder()
            .notificationEnabled(JsonNullable.of(true))
            .notificationRadius(JsonNullable.of(15))
            .build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    ThrowingCallable action =
        () -> userService.update(target.getUsername(), dto, issuer.getUsername());

    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotEditableException.class);
  }

  // --- UserService#updatePassword

  @DisplayName("UserService#updatePassword should update user password")
  @Test
  void updatePassword_should_update_user_password() {
    // Given
    var target =
        UserEntity.builder()
            .username("target")
            .password(passwordEncoder.encode("Password123!"))
            .build();
    var dto =
        UpdatePasswordDto.builder().oldPassword("Password123!").newPassword("Password456!").build();

    // When
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    userService.updatePassword(target.getUsername(), dto, target.getUsername());

    // Then
    verify(passwordEncoder, times(1)).encode(dto.getNewPassword());
    verify(userRepository, times(1)).save(isA(UserEntity.class));
  }

  @DisplayName("UserService#updatePassword should throw when dto is invalid")
  @Test
  void updatePassword_should_throw_when_dto_is_invalid() {
    // Given
    var dto = UpdatePasswordDto.builder().oldPassword(null).newPassword("a").build();

    // When
    ThrowingCallable action = () -> userService.updatePassword("target", dto, "target");

    // Then
    assertThatThrownBy(action)
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue(
            "violations", Set.of("oldPassword.required", "newPassword.size"));
  }

  @DisplayName("UserService#updatePassword should throw when issuer can't edit target")
  @Test
  void updatePassword_should_throw_when_issuer_cant_edit_target() {
    // Given
    var target =
        UserEntity.builder()
            .username("target")
            .password(passwordEncoder.encode("Password123!"))
            .build();
    var dto =
        UpdatePasswordDto.builder().oldPassword("Password123!").newPassword("Password456!").build();
    var issuer = UserEntity.builder().username("issuer").build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    ThrowingCallable action =
        () -> userService.updatePassword(target.getUsername(), dto, issuer.getUsername());

    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotEditableException.class);
  }

  @DisplayName("UserService#updatePassword should throw when paswords don't match")
  @Test
  void updatePassword_should_throw_when_passwords_dont_match() {
    // Given
    var target =
        UserEntity.builder()
            .username("target")
            .password(passwordEncoder.encode("Password123!"))
            .build();
    var dto =
        UpdatePasswordDto.builder().oldPassword("Password456!").newPassword("Password456!").build();

    // When
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    ThrowingCallable action =
        () -> userService.updatePassword(target.getUsername(), dto, target.getUsername());

    // Then
    assertThatThrownBy(action).isInstanceOf(PasswordMismatchException.class);
  }

  // --- UserService#updatePosition

  @DisplayName("UserService#updatePosition should update user position")
  @Test
  void updatePosition_should_update_user_position() {
    // Given
    var target = UserEntity.builder().username("target").build();
    var dto = UpdatePositionDto.builder().latitude(3.402892).longitude(22.39392).build();
    var issuer = UserEntity.builder().username("issuer").type(Type.ADMIN).build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    userService.updatePosition(target.getUsername(), dto, issuer.getUsername());

    // Then
    verify(userRepository, times(1)).save(isA(UserEntity.class));
  }

  @DisplayName("UserService#updatePosition should throw when dto is invalid")
  @Test
  void updatePosition_should_throw_when_dto_is_invalid() {
    // Given
    var dto = UpdatePositionDto.builder().latitude(110.0).longitude(420.0).build();

    // When
    ThrowingCallable action = () -> userService.updatePosition("target", dto, "issuer");

    // Then
    assertThatThrownBy(action)
        .isInstanceOf(ValidationException.class)
        .hasFieldOrPropertyWithValue("violations", Set.of("latitude.range", "longitude.range"));
  }

  @DisplayName("UserService#updatePosition should throw when issuer can't edit target")
  @Test
  void updatePosition_should_throw_when_issuer_cant_edit_target() {
    // Given
    var target = UserEntity.builder().username("target").build();
    var dto = UpdatePositionDto.builder().latitude(3.402892).longitude(22.39392).build();
    var issuer = UserEntity.builder().username("issuer").build();

    // When
    when(userRepository.findByUsernameIgnoreCase(issuer.getUsername()))
        .thenReturn(Optional.of(issuer));
    when(userRepository.findByUsernameIgnoreCase(target.getUsername()))
        .thenReturn(Optional.of(target));
    ThrowingCallable action =
        () -> userService.updatePosition(target.getUsername(), dto, issuer.getUsername());

    // Then
    assertThatThrownBy(action).isInstanceOf(UserNotEditableException.class);
  }
}
