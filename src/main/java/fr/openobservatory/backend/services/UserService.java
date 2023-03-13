package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.ChangePasswordDto;
import fr.openobservatory.backend.dto.ProfileDto;
import fr.openobservatory.backend.dto.RegisterUserDto;
import fr.openobservatory.backend.dto.UserDto;
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

  public ProfileDto getProfile(String targetedUser, String currentUser) {
    var user = userRepository.findByUsernameIgnoreCase(targetedUser).orElseThrow(UnknownUserException::new);
    if (!user.isPublic() && (!Objects.equals(currentUser, targetedUser))) {
      throw new ProfileNotAccessibleException();
    }
  }
}
