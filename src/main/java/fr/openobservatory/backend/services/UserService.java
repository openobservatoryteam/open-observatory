package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.*;
import fr.openobservatory.backend.entities.User;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.UserRepository;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

  private final ModelMapper modelMapper;
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;

  // ---

  public Optional<User> findByUsername(String username) {
    return userRepository
        .findByUsernameIgnoreCase(username)
        .map(u -> modelMapper.map(u, User.class));
  }

  public User register(RegisterUserDto dto) {
    if (!Pattern.matches(UserEntity.USERNAME_PATTERN, dto.getUsername()))
      throw new InvalidUsernameException();
    if (userRepository.existsByUsernameIgnoreCase(dto.getUsername()))
      throw new UsernameAlreadyUsedException();
    var entity = new UserEntity();
    entity.setUsername(dto.getUsername());
    entity.setPassword(passwordEncoder.encode(dto.getPassword()));
    entity.setBiography(dto.getBiography());
    entity.setType(UserEntity.Type.USER);
    entity.setPublic(true);
    entity.setCreatedAt(Instant.now());
    return modelMapper.map(userRepository.save(entity), User.class);
  }

  public void modifyPassword(String username, ChangePasswordDto dto) {
    var user =
        userRepository.findByUsernameIgnoreCase(username).orElseThrow(UnknownUserException::new);
    if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
      throw new InvalidPasswordException();
    }
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userRepository.save(user);
  }

  public User getProfile(String targetedUser) {
    var user =
        userRepository
            .findByUsernameIgnoreCase(targetedUser)
            .orElseThrow(UnknownUserException::new);
    if (!user.isPublic()) {
      throw new ProfileNotAccessibleException();
    }
    return modelMapper.map(user, User.class);
  }

  public User updateProfile(String currentUser, UpdateProfileDto dto) {
    var user =
        userRepository.findByUsernameIgnoreCase(currentUser).orElseThrow(UnknownUserException::new);
    user.setBiography(dto.getBiography());
    return userRepository.save(user);
  }

  public boolean canEditUser(String targetedUser, String currentUser) {
    return Objects.equals(targetedUser, currentUser);
  }

  public boolean isViewable(String targetedUser, String currentUser) {
    var user =
        userRepository
            .findByUsernameIgnoreCase(targetedUser)
            .orElseThrow(UnknownUserException::new);
    return user.isPublic() || Objects.equals(targetedUser, currentUser);
  }
}
