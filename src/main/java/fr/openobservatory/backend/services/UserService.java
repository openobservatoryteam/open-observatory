package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.input.CreateUserDto;
import fr.openobservatory.backend.dto.input.UpdatePasswordDto;
import fr.openobservatory.backend.dto.input.UpdatePositionDto;
import fr.openobservatory.backend.dto.input.UpdateUserDto;
import fr.openobservatory.backend.dto.output.ObservationWithDetailsDto;
import fr.openobservatory.backend.dto.output.SelfUserDto;
import fr.openobservatory.backend.dto.output.UserWithProfileDto;
import fr.openobservatory.backend.entities.UserEntity;
import fr.openobservatory.backend.exceptions.*;
import fr.openobservatory.backend.repositories.ObservationRepository;
import fr.openobservatory.backend.repositories.PushSubscriptionRepository;
import fr.openobservatory.backend.repositories.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Validator;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Generated;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class UserService {

  private final ModelMapper modelMapper;
  private final ObservationRepository observationRepository;
  private final PasswordEncoder passwordEncoder;
  private final PushSubscriptionRepository pushSubscriptionRepository;
  private final UserRepository userRepository;
  private final Validator validator;

  // ---

  public UserWithProfileDto create(CreateUserDto dto) {
    var violations = validator.validate(dto);
    if (!violations.isEmpty()) throw new ValidationException(violations);
    if (userRepository.existsByUsernameIgnoreCase(dto.getUsername()))
      throw new UsernameAlreadyUsedException();
    var user =
        UserEntity.builder()
            .username(dto.getUsername())
            .password(passwordEncoder.encode(dto.getPassword()))
            .biography(dto.getBiography())
            .build();
    return buildProfile(userRepository.save(user), UserWithProfileDto.class);
  }

  public UserWithProfileDto findByUsername(String username, String issuerUsername) {
    var issuer = findIssuer(issuerUsername, true);
    var user =
        userRepository.findByUsernameIgnoreCase(username).orElseThrow(UnknownUserException::new);
    if (!isViewableBy(user, issuer)) throw new UserNotVisibleException();
    return buildProfile(user, UserWithProfileDto.class);
  }

  // TODO: Move to ObservationService
  @Generated
  public List<ObservationWithDetailsDto> findObservationsByUsername(
      String username, String issuerUsername) {
    var issuer =
        issuerUsername != null
            ? userRepository
                .findByUsernameIgnoreCase(issuerUsername)
                .orElseThrow(UnavailableUserException::new)
            : null;
    var user =
        userRepository.findByUsernameIgnoreCase(username).orElseThrow(UnknownUserException::new);
    if (!isViewableBy(user, issuer)) throw new UserNotVisibleException();
    return observationRepository.findAllByAuthor(user, Pageable.ofSize(100)).stream()
        .map(
            o -> {
              var dto = modelMapper.map(o, ObservationWithDetailsDto.class);
              dto.setExpired(
                  o.getTimestamp()
                      .plus(o.getCelestialBody().getValidityTime(), ChronoUnit.HOURS)
                      .isBefore(Instant.now()));
              return dto;
            })
        .toList();
  }

  public SelfUserDto findSelf(String issuerUsername) {
    return userRepository
        .findByUsernameIgnoreCase(issuerUsername)
        .map(u -> buildProfile(u, SelfUserDto.class))
        .orElseThrow(UnavailableUserException::new);
  }

  @Transactional
  public SelfUserDto update(String username, UpdateUserDto dto, String issuerUsername) {
    var violations = validator.validate(dto);
    if (!violations.isEmpty()) throw new ValidationException(violations);
    var issuer = findIssuer(issuerUsername, false);
    var user =
        userRepository.findByUsernameIgnoreCase(username).orElseThrow(UnknownUserException::new);
    if (!isEditableBy(user, issuer)) throw new UserNotEditableException();
    if (issuer.getType() == UserEntity.Type.ADMIN && dto.getPassword().isPresent()) {
      var password = passwordEncoder.encode(dto.getPassword().get());
      user.setPassword(password);
    }
    if (dto.getAvatar().isPresent()) {
      user.setAvatar(dto.getAvatar().get());
    }
    if (dto.getBiography().isPresent()) {
      user.setBiography(dto.getBiography().get());
    }
    if (dto.getIsPublic().isPresent()) {
      user.setPublic(dto.getIsPublic().get());
    }
    if (dto.getNotificationEnabled().isPresent()) {
      user.setNotificationEnabled(dto.getNotificationEnabled().get());
      if (!user.isNotificationEnabled()) pushSubscriptionRepository.deleteAllByUser(user);
    }
    if (dto.getNotificationRadius().isPresent()) {
      user.setNotificationRadius(dto.getNotificationRadius().get());
    }
    return buildProfile(userRepository.save(user), SelfUserDto.class);
  }

  public void updatePassword(String username, UpdatePasswordDto dto, String issuerUsername) {
    var violations = validator.validate(dto);
    if (!violations.isEmpty()) throw new ValidationException(violations);
    var issuer = findIssuer(issuerUsername, false);
    var user =
        userRepository.findByUsernameIgnoreCase(username).orElseThrow(UnknownUserException::new);
    if (!isEditableBy(user, issuer)) throw new UserNotEditableException();
    if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
      throw new PasswordMismatchException();
    }
    user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    userRepository.save(user);
  }

  public void updatePosition(String username, UpdatePositionDto dto, String issuerUsername) {
    var violations = validator.validate(dto);
    if (!violations.isEmpty()) throw new ValidationException(violations);
    var issuer = findIssuer(issuerUsername, false);
    var user =
        userRepository.findByUsernameIgnoreCase(username).orElseThrow(UnknownUserException::new);
    if (!isEditableBy(user, issuer)) throw new UserNotEditableException();
    user.setPositionAt(Instant.now());
    user.setLatitude(dto.getLatitude());
    user.setLongitude(dto.getLongitude());
    userRepository.save(user);
  }

  // ---

  private <T extends UserWithProfileDto> T buildProfile(UserEntity user, Class<T> clazz) {
    var observations = observationRepository.findAllByAuthor(user);
    var dto = modelMapper.map(user, clazz);
    dto.setKarma(
        observations.stream()
            .map(
                o ->
                    o.getVotes().stream().map(v -> v.getVote().getWeight()).reduce(0, Integer::sum))
            .reduce(0, Integer::sum));
    return dto;
  }

  private UserEntity findIssuer(String issuerUsername, boolean allowGuest) {
    if (allowGuest && issuerUsername == null) return null;
    return userRepository
        .findByUsernameIgnoreCase(issuerUsername)
        .orElseThrow(UnavailableUserException::new);
  }

  private boolean isEditableBy(UserEntity targetedUser, UserEntity issuer) {
    return issuer.getType().equals(UserEntity.Type.ADMIN) || targetedUser.equals(issuer);
  }

  private boolean isViewableBy(UserEntity targetedUser, UserEntity issuer) {
    return (issuer != null && issuer.getType().equals(UserEntity.Type.ADMIN))
        || targetedUser.isPublic()
        || targetedUser.equals(issuer);
  }
}
