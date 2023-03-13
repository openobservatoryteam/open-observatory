package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.*;
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

  public Optional<UserDto> findByUsername(String username) {
    return userRepository
        .findByUsernameIgnoreCase(username)
        .map(u -> modelMapper.map(u, UserDto.class));
  }

  public UserDto register(RegisterUserDto dto) {
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
    return modelMapper.map(userRepository.save(entity), UserDto.class);
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

  public ProfileDto getProfile(String targetedUser) {
    var user =
        userRepository
            .findByUsernameIgnoreCase(targetedUser)
            .orElseThrow(UnknownUserException::new);
    if (!user.isPublic()) {
      throw new ProfileNotAccessibleException();
    }
    var profile = new ProfileDto();
    profile.setBiography(user.getBiography());
    profile.setKarma(0);
    profile.setAchievements(new AchievementDto[] {new AchievementDto()});
    var userDto = findByUsername(targetedUser).orElseThrow(InvalidUsernameException::new);
    profile.setUser(userDto);
    return profile;
  }

  public ProfileDto updateProfile(String currentUser, UpdateProfileDto dto) {
    var user =
        userRepository.findByUsernameIgnoreCase(currentUser).orElseThrow(UnknownUserException::new);
    user.setBiography(dto.getBiography());
    userRepository.save(user);
    return getProfile(currentUser);
  }

  public Boolean canEditUser(String targetedUser, String currentUser) {
    return Objects.equals(targetedUser, currentUser);
  }

  public Boolean isViewable(String targetedUser, String currentUser) {
    if (!userRepository
        .findByUsernameIgnoreCase(targetedUser)
        .orElseThrow(UnknownUserException::new)
        .isPublic()) {
      return !Objects.equals(targetedUser, currentUser);
    }
    return true;
  }
}
