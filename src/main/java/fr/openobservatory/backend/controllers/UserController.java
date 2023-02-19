package fr.openobservatory.backend.controllers;


import fr.openobservatory.backend.dto.RegisterUserDto;
import fr.openobservatory.backend.dto.UserDto;
import fr.openobservatory.backend.models.User;
import fr.openobservatory.backend.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {

    private UserService userService;


    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@ModelAttribute("RegisterDto")RegisterUserDto dto) {
        User user = userService.registerAccount(dto);
        UserDto userDto = new UserDto();
        userDto.setUsername(user.getUsername());
        userDto.setAvatarUrl(user.getAvatarUrl());
        return ResponseEntity.ok().body(userDto);
    }

}
