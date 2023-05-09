package fr.openobservatory.backend.controllers;

import fr.openobservatory.backend.dto.input.*;
import fr.openobservatory.backend.dto.output.*;
import fr.openobservatory.backend.services.UserService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@Tag(name = "Users routes", description = "All user's related routes")
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  // ---
  @Operation(summary = "Get all users")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Get all users"),
        @ApiResponse(
            responseCode = "400",
            description = "Incorrect user's number per page OR incorrect page number")
      })
  @GetMapping
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<SearchResultsDto<UserWithProfileDto>> search(PaginationDto dto) {
    if (dto.getItemsPerPage() == null) dto.setItemsPerPage(10);
    if (dto.getPage() == null) dto.setPage(0);
    var users = userService.search(dto);
    return ResponseEntity.ok(users);
  }

  @Operation(summary = "Register as a new user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "User's registration confirmed"),
        @ApiResponse(
            responseCode = "400",
            description =
                "Request body format is not \"application/json\" OR Request body don't contain username or password OR Username's format is not correct OR Password is not strong enough OR Username is already used")
      })
  @PostMapping("/register")
  @PreAuthorize("isAnonymous()")
  public ResponseEntity<UserWithProfileDto> register(@RequestBody CreateUserDto dto) {
    var user = userService.create(dto);
    return ResponseEntity.created(URI.create("/users/" + user.getUsername())).body(user);
  }

  @Operation(summary = "Get actual authenticated user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Return authenticated user"),
        @ApiResponse(responseCode = "503", description = "User couldn't be retrieved")
      })
  @GetMapping("/@me")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<SelfUserDto> current(Authentication authentication) {
    var user = userService.findSelf(authentication.getName());
    return ResponseEntity.ok(user);
  }

  @Operation(summary = "Get user associated to given username")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Return user associated to given username"),
        @ApiResponse(
            responseCode = "400",
            description = "Parameter \"username\" is not a valid string"),
        @ApiResponse(
            responseCode = "403",
            description =
                "Targeted user profile is private and actual user hasn't permission to access it")
      })
  @GetMapping("/{username}")
  public ResponseEntity<UserWithProfileDto> findByUsername(
      Authentication authentication, @PathVariable String username) {
    var issuerUsername = authentication == null ? null : authentication.getName();
    var user = userService.findByUsername(username, issuerUsername);
    return ResponseEntity.ok(user);
  }

  @Operation(summary = "Get history of user associated to given username (with pages numbering)")
  @ApiResponses(
      value = {
        @ApiResponse(
            responseCode = "200",
            description = "Get a certain page of user's observations history"),
        @ApiResponse(
            responseCode = "400",
            description = "Incorrect observation's number per page OR incorrect page number"),
        @ApiResponse(
            responseCode = "403",
            description =
                "Current authenticated user is not allowed to access history (current user is not administrator or targeted user is not current user and targeted user's profile is private"),
        @ApiResponse(responseCode = "404", description = "User with given username can't be find")
      })
  @GetMapping("/{username}/observations")
  public ResponseEntity<List<ObservationWithDetailsDto>> findObservationsByUsername(
      Authentication authentication, @PathVariable String username) {
    var issuerUsername = authentication == null ? null : authentication.getName();
    var observations = userService.findObservationsByUsername(username, issuerUsername);
    return ResponseEntity.ok(observations);
  }

  @Operation(summary = "Modify user profile")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "User's profile modified successfully"),
        @ApiResponse(
            responseCode = "400",
            description =
                "Request body format is not \"application/json\" OR Parameter \"username\" is not a valid string OR Parameter avatar is not a valid image OR New username's format is not correct OR New username is already used"),
        @ApiResponse(
            responseCode = "403",
            description = "Current user is not targeted user or administrator")
      })
  @PatchMapping("/{username}")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<UserWithProfileDto> update(
      Authentication authentication,
      @PathVariable String username,
      @RequestBody UpdateUserDto dto) {
    var user = userService.update(username, dto, authentication.getName());
    return ResponseEntity.ok(user);
  }

  @Operation(summary = "Change targeted user's password")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Password modified successfully"),
        @ApiResponse(
            responseCode = "400",
            description =
                "Request body format is not \"application/json\" OR Parameter \"username\" is not a valid string OR Request body don't contain currentPassword or newPassword OR currentPassword don't match targeted user password OR newPassword is not strong enough"),
        @ApiResponse(responseCode = "403", description = "Current user is not targeted user"),
        @ApiResponse(responseCode = "404", description = "User with given username can't be find")
      })
  @PatchMapping("/{username}/password")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> changePassword(
      Authentication authentication,
      @PathVariable String username,
      @RequestBody UpdatePasswordDto dto) {
    userService.updatePassword(username, dto, authentication.getName());
    return ResponseEntity.noContent().build();
  }

  @Hidden
  @PostMapping("/{username}/position")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<Void> updateUserPosition(
      Authentication authentication,
      @PathVariable String username,
      @RequestBody UpdatePositionDto dto) {
    userService.updatePosition(username, dto, authentication.getName());
    return ResponseEntity.noContent().build();
  }

  @Operation(summary = "Delete user")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "User successfully deleted"),
        @ApiResponse(
            responseCode = "400",
            description = "Parameter \"username\" is not a valid string"),
        @ApiResponse(responseCode = "403", description = "Current user is not administrator"),
        @ApiResponse(responseCode = "404", description = "User with given username can't be find")
      })
  @DeleteMapping("/{username}")
  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  public ResponseEntity<Void> delete(Authentication authentication, @PathVariable String username) {
    userService.delete(username, authentication.getName());
    return ResponseEntity.noContent().build();
  }
}
