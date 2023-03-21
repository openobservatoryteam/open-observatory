package services;

import static org.assertj.core.api.Assertions.*;

import fr.openobservatory.backend.dto.ChangePasswordDto;
import fr.openobservatory.backend.dto.RegisterUserDto;
import fr.openobservatory.backend.dto.UpdateProfileDto;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.UserRepository;
import fr.openobservatory.backend.services.UserService;
import java.time.Instant;
import java.util.Optional;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Spy private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

  @Mock private UserRepository userRepository;

  @InjectMocks private UserService userService;

  // --- UserService#canEditUser

  @DisplayName("UserService#canEditUser should return true with matching usernames")
  @Test
  void can_edit_user_should_return_true_with_matching_usernames() {
    // Given
    var targetedUser = "Eikjos_TV";
    var currentUser = "Eikjos_TV";

    // When
    var decision = userService.canEditUser(targetedUser, currentUser);

    // Then
    assertThat(decision).isTrue();
  }

  @DisplayName("UserService#canEditUser should return false with mismatching usernames")
  @Test
  void can_edit_user_should_return_false_with_mismatching_usernames() {
    // Given
    var targetedUser = "Eikjos_TV";
    var currentUser = "Coucoba";

    // When
    var decision = userService.canEditUser(targetedUser, currentUser);

    // Then
    assertThat(decision).isFalse();
  }

  // --- UserService#findByUsername

  @DisplayName("UserService#findByUsername should return found user with existing username")
  @Test
  void find_by_username_should_return_found_user_with_existing_username() {
    // Given
    var id = 1L;
    var username = "eikjos_tv";

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(Mockito.anyString()))
        .thenAnswer(
            answer -> {
              var entity = new UserEntity();
              entity.setId(id);
              entity.setUsername("Eikjos_TV");
              return Optional.of(entity);
            });
    var user = userService.findByUsername(username);

    // Then
    assertThat(user).isPresent();
    assertThat(user.get().getId()).isEqualTo(id);
    assertThat(user.get().getUsername()).isEqualToIgnoringCase(username);
  }

  @DisplayName("UserService#findByUsername should return nothing with unknown username")
  @Test
  void find_by_username_should_return_nothing_with_unknown_username() {
    // Given
    var username = "eikjos_tv";

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
    var user = userService.findByUsername(username);

    // Then
    assertThat(user).isNotPresent();
  }

  // --- UserService#getProfile

  @DisplayName("UserService#getProfile should return profile for existing user with public profile")
  @Test
  void get_profile_should_return_profile_for_existing_user_and_public_profile() {
    // Given
    var biography = "I'm Coucoba, an Open Observatory developer";
    var id = 1L;
    var username = "Coucoba";

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(username))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setId(id);
              user.setUsername(username);
              user.setBiography(biography);
              user.setPublic(true);
              return Optional.of(user);
            });
    var user = userService.getProfile(username);

    // Then
    assertThat(user.getId()).isEqualTo(id);
    assertThat(user.getUsername()).isEqualTo(username);
    assertThat(user.getBiography()).isEqualTo(biography);
  }

  @DisplayName("UserService#getProfile should fail for unknown user")
  @Test
  public void get_profile_should_fail_for_unknown_user() {
    // Given
    var username = "Coucoba";

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
    ThrowableAssert.ThrowingCallable action = () -> userService.getProfile(username);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownUserException.class);
  }

  @DisplayName("UserService#getProfile should fail for existing user with private profile")
  @Test
  void get_profile_should_fail_for_existing_user_and_private_profile() {
    // Given
    var username = "Coucoba";

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(username))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setId(1L);
              user.setUsername(username);
              user.setPublic(false);
              return Optional.of(user);
            });
    ThrowableAssert.ThrowingCallable action = () -> userService.getProfile(username);

    // Then
    assertThatThrownBy(action).isInstanceOf(ProfileNotAccessibleException.class);
  }

  // --- UserService#isViewable

  @DisplayName("UserService#isViewable should return true with public profile")
  @Test
  void is_viewable_should_return_true_with_public_profile() {
    // Given
    var targetedUser = "Eikjos_TV";
    var currentUser = "Coucoba";

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(targetedUser))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setId(1L);
              user.setUsername(answer.getArgument(0, String.class));
              user.setPublic(true);
              return Optional.of(user);
            });
    var decision = userService.isViewable(targetedUser, currentUser);

    // Then
    assertThat(decision).isTrue();
  }

  @DisplayName("UserService#isViewable should return true with matching usernames")
  @Test
  void is_viewable_should_return_true_with_matching_usernames() {
    // Given
    var targetedUser = "Coucoba";
    var currentUser = "Coucoba";

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(targetedUser))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setId(1L);
              user.setUsername(targetedUser);
              return Optional.of(user);
            });
    var decision = userService.isViewable(targetedUser, currentUser);

    // Then
    assertThat(decision).isTrue();
  }

  @DisplayName(
      "UserService#isViewable should return false with mismatching usernames and private profile")
  @Test
  void is_viewable_should_return_false_with_mismatching_usernames_and_private_profile() {
    // Given
    var targetedUser = "Eikjos_TV";
    var currentUser = "Coucoba";

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(targetedUser))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setId(1L);
              user.setUsername(targetedUser);
              user.setPublic(false);
              return Optional.of(user);
            });
    var decision = userService.isViewable(targetedUser, currentUser);

    // Then
    assertThat(decision).isFalse();
  }

  @DisplayName("UserService#isViewable should fail with unknown targeted user")
  @Test
  void is_viewable_should_fail_with_unknown_targeted_user() {
    // Given
    var targetedUser = "Eikjos_TV";
    var currentUser = "Coucoba";

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(targetedUser))
        .thenReturn(Optional.empty());
    ThrowableAssert.ThrowingCallable action =
        () -> userService.isViewable(targetedUser, currentUser);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownUserException.class);
  }

  // --- UserService#modifyPassword

  @DisplayName("UserService#modifyPassword should pass with valid credentials and valid input")
  @Test
  void modify_password_should_pass_with_valid_credentials_and_valid_input() {
    // Given
    var username = "Eikjos_TV";
    var oldPassword = "thisIsMyCurrentPassword";
    var newPassword = "thisIsMyNewPassword";
    var dto = new ChangePasswordDto(oldPassword, newPassword);

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(username))
        .thenAnswer(
            answer -> {
              if (!"Eikjos_TV".equalsIgnoreCase(answer.getArgument(0, String.class)))
                return Optional.empty();
              var entity = new UserEntity();
              entity.setId(1L);
              entity.setUsername(username);
              entity.setPassword(passwordEncoder.encode(oldPassword));
              return Optional.of(entity);
            });
    Mockito.when(userRepository.save(Mockito.isA(UserEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0, UserEntity.class));
    ThrowableAssert.ThrowingCallable action = () -> userService.modifyPassword(username, dto);

    // Then
    assertThatNoException().isThrownBy(action);
  }

  @DisplayName("UserService#modifyPassword should fail with an invalid username")
  @Test
  void modify_password_should_fail_with_an_invalid_username() {
    // Given
    var username = "Eikjos_TV";
    var oldPassword = "thisIsMyCurrentPassword";
    var newPassword = "thisIsMyNewPassword";
    var dto = new ChangePasswordDto(oldPassword, newPassword);

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(Mockito.anyString()))
        .thenReturn(Optional.empty());
    ThrowableAssert.ThrowingCallable action = () -> userService.modifyPassword(username, dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownUserException.class);
  }

  @DisplayName("UserService#modifyPassword should fail with a password mismatch")
  @Test
  void modify_password_should_fail_with_a_password_mismatch() {
    // Given
    var username = "Eikjos_TV";
    var oldPassword = "thisIsMyCurrentPassword";
    var newPassword = "thisIsMyNewPassword";
    var dto = new ChangePasswordDto(oldPassword, newPassword);

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(username))
        .thenAnswer(
            answer -> {
              var entity = new UserEntity();
              entity.setId(1L);
              entity.setUsername(username);
              entity.setPassword(passwordEncoder.encode("invalid password"));
              return Optional.of(entity);
            });
    ThrowableAssert.ThrowingCallable action = () -> userService.modifyPassword(username, dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidPasswordException.class);
  }

  // --- UserService#register

  @DisplayName("UserService#register should return registered user with valid input")
  @Test
  void register_should_return_registered_user_with_valid_input() {
    // Given
    var id = 1L;
    var dto = new RegisterUserDto("xX_superuser123_Xx", "myamazingpassword", "Hello, I'm a user!");

    // When
    Mockito.when(userRepository.save(Mockito.isA(UserEntity.class)))
        .thenAnswer(
            answer -> {
              var entity = answer.getArgument(0, UserEntity.class);
              entity.setId(1L);
              entity.setType(UserEntity.Type.USER);
              entity.setCreatedAt(Instant.now());
              return entity;
            });
    var user = userService.register(dto);

    // Then
    assertThat(user.getId()).isEqualTo(id);
    assertThat(user.getUsername()).isEqualTo(dto.getUsername());
    assertThat(user.getPassword()).isNotEqualTo(dto.getPassword());
    assertThat(user.getAvatar()).isNull();
    assertThat(user.getType()).isEqualTo(UserEntity.Type.USER);
    assertThat(user.getCreatedAt()).isBefore(Instant.now());
  }

  @DisplayName("UserService#register should fail with an invalid username")
  @Test
  void register_should_fail_with_an_invalid_username() {
    // Given
    var dto = new RegisterUserDto("this is not a good username", "somepassword256", "Hello World!");

    // When
    ThrowableAssert.ThrowingCallable action = () -> userService.register(dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(InvalidUsernameException.class);
  }

  @DisplayName("UserService#register should fail with an existing username")
  @Test
  void register_should_fail_with_an_existing_username() {
    Mockito.when(userRepository.existsByUsernameIgnoreCase(Mockito.isA(String.class)))
        .thenReturn(true);

    // Given
    var dto = new RegisterUserDto("xX_superuser123_Xx", "myamazingpassword", "Hello, I'm a user!");

    // When
    ThrowableAssert.ThrowingCallable action = () -> userService.register(dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(UsernameAlreadyUsedException.class);
  }

  // --- UserService#updateProfile

  @DisplayName("UserService#updateProfile should return updated profile for existing user")
  @Test
  void update_profile_should_return_updated_profile_for_existing_user() {
    // Given
    var currentBiography = "I'm Coucoba, an Open Observatory developer";
    var newBiography = "This is my new biography";
    var username = "Coucoba";
    var dto = new UpdateProfileDto(newBiography);

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(username))
        .thenAnswer(
            answer -> {
              var user = new UserEntity();
              user.setId(1L);
              user.setUsername(username);
              user.setBiography(currentBiography);
              user.setPublic(true);
              return Optional.of(user);
            });
    Mockito.when(userRepository.save(Mockito.isA(UserEntity.class)))
        .thenAnswer(answer -> answer.getArgument(0, UserEntity.class));
    var user = userService.updateProfile(username, dto);

    // Then
    assertThat(user.getBiography()).isEqualTo(newBiography);
  }

  @DisplayName("UserService#updateProfile should fail for unknown user")
  @Test
  void update_profile_should_fail_for_unknown_user() {
    // Given
    var username = "Coucoba";
    var dto = new UpdateProfileDto("This is my new biography");

    // When
    Mockito.when(userRepository.findByUsernameIgnoreCase(username)).thenReturn(Optional.empty());
    ThrowableAssert.ThrowingCallable action = () -> userService.updateProfile(username, dto);

    // Then
    assertThatThrownBy(action).isInstanceOf(UnknownUserException.class);
  }
}
