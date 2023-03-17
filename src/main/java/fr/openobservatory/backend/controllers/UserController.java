package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.*;
import fr.openobservatory.backend.services.ObservationService;
import fr.openobservatory.backend.services.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;
  private final ObservationService observationService;
  private final ModelMapper modelMapper;

  // ---

  @GetMapping("/current")
  public ResponseEntity<UserDto> current(Authentication authentication) {
    var user =
        userService
            .findByUsername(authentication.getName())
            .map(u -> modelMapper.map(u, UserDto.class));
    return ResponseEntity.of(user);
  }

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@RequestBody @Valid RegisterUserDto dto) {
    var user = modelMapper.map(userService.register(dto), UserDto.class);
    return new ResponseEntity<>(user, HttpStatus.CREATED);
  }

  @PatchMapping("/{username}/password")
  public ResponseEntity<Void> changePassword(
      Authentication authentication,
      @RequestBody @Valid ChangePasswordDto dto,
      @PathVariable("username") String username) {
    if (!userService.canEditUser(username, authentication.getName())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    userService.modifyPassword(authentication.getName(), dto);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/{username}")
  public ResponseEntity<UserWithProfileDto> getProfile(
      Authentication authentication, @PathVariable("username") String username) {
    if (!userService.isViewable(username, authentication.getName())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    var profile = modelMapper.map(userService.getProfile(username), UserWithProfileDto.class);
    return new ResponseEntity<>(profile, HttpStatus.OK);
  }

  @PatchMapping("/{username}")
  public ResponseEntity<UserWithProfileDto> updateProfile(
      Authentication authentication,
      @PathVariable("username") String username,
      @RequestBody @Valid UpdateProfileDto dto) {
    if (!userService.canEditUser(username, authentication.getName())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    var profile =
        modelMapper.map(userService.updateProfile(username, dto), UserWithProfileDto.class);
    return new ResponseEntity<>(profile, HttpStatus.OK);
  }

  @GetMapping("/{username}/observations")
  public ResponseEntity<List<ObservationDto>> getObservations(
      Authentication authentication, @PathVariable("username") String username) {
    if (!userService.isViewable(username, authentication.getName())) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    var observations =
        observationService.findObservationsByAuthor(username).stream()
            .map(o -> modelMapper.map(o, ObservationDto.class))
            .toList();
    return new ResponseEntity<>(observations, HttpStatus.OK);
  }
}
