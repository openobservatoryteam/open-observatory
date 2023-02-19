package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.RegisterUserDto;
import fr.openobservatory.backend.dto.UserDto;
import fr.openobservatory.backend.services.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

  private UserService userService;

  // ---

  @PostMapping("/register")
  public ResponseEntity<UserDto> register(@RequestBody @Valid RegisterUserDto dto) {
    var user = userService.register(dto);
    return new ResponseEntity<>(user, HttpStatus.CREATED);
  }
}
