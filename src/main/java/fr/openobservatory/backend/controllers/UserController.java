package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.*;
import fr.openobservatory.backend.services.UserService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  // ---

  @PostMapping("/register")
  @PreAuthorize("isAnonymous()")
  public ResponseEntity<UserWithProfileDto> register(@RequestBody @Valid CreateUserDto dto) {
    var user = userService.create(dto);
    return ResponseEntity.created(URI.create("/users/" + user.getUsername())).body(user);
  }

  @GetMapping("/current")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UserWithProfileDto> current(Authentication authentication) {
    var user = userService.findSelf(authentication.getName());
    return ResponseEntity.ok(user);
  }

  @GetMapping("/{username}")
  public ResponseEntity<UserWithProfileDto> findByUsername(
      Authentication authentication, @PathVariable String username) {
    var issuerUsername = authentication == null ? null : authentication.getName();
    var user = userService.findByUsername(username, issuerUsername);
    return ResponseEntity.ok(user);
  }

  @GetMapping("/{username}/observations")
  public ResponseEntity<List<ObservationDto>> findObservationsByUsername(
      Authentication authentication, @PathVariable String username) {
    var issuerUsername = authentication == null ? null : authentication.getName();
    var observations = userService.findObservationsByUsername(username, issuerUsername);
    return ResponseEntity.ok(observations);
  }

  @PatchMapping("/{username}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UserWithProfileDto> update(
      Authentication authentication,
      @PathVariable String username,
      @RequestBody @Valid UpdateProfileDto dto) {
    var user = userService.update(username, dto, authentication.getName());
    return ResponseEntity.ok(user);
  }

  @PatchMapping("/{username}/password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> changePassword(
      Authentication authentication,
      @PathVariable String username,
      @RequestBody @Valid ChangePasswordDto dto) {
    userService.modifyPassword(username, dto, authentication.getName());
    return ResponseEntity.noContent().build();
  }
}
