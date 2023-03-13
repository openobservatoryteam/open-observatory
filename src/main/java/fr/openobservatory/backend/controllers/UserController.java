package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.ChangePasswordDto;
import fr.openobservatory.backend.dto.ProfileDto;
import fr.openobservatory.backend.dto.RegisterUserDto;
import fr.openobservatory.backend.dto.UserDto;
import fr.openobservatory.backend.exceptions.UnknownUserException;
import fr.openobservatory.backend.services.UserService;
import jakarta.validation.Valid;
import java.util.Objects;
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

  private UserService userService;
  private PasswordEncoder passwordEncoder;

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
    if (!(Objects.equals(username, authentication.getName()))) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
    userService.modifyPassword(authentication.getName(), dto);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

  @GetMapping("/{username}/profile")
  public ResponseEntity<ProfileDto> getProfile(Authentication authentication,@PathVariable("username") String username) {
    var profile = userService.
    return ResponseEntity<>(profile, HttpStatus.OK);
  }

}
