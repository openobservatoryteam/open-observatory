package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.*;
import fr.openobservatory.backend.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  // ---

  @GetMapping("/current")
  public ResponseEntity<UserDto> current(Authentication authentication) {
    var user = userService.findByUsername(authentication.getName());
    return ResponseEntity.of(user);
  }

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@RequestBody @Valid RegisterUserDto dto) {
    var user = userService.register(dto);
    return new ResponseEntity<>(user, HttpStatus.CREATED);
  }

  @PatchMapping("/{username}/password")
  public ResponseEntity<?> changePassword(
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
    var profile = userService.getProfile(username);
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
    var profile = userService.updateProfile(username, dto);
    return new ResponseEntity<>(profile, HttpStatus.OK);
  }
}
