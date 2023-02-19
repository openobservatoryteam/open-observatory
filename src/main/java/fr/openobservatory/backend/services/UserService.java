package fr.openobservatory.backend.services;

import fr.openobservatory.backend.dto.RegisterUserDto;
import fr.openobservatory.backend.exceptions.UsernameAlreadyUsedException;
import fr.openobservatory.backend.models.User;
import fr.openobservatory.backend.models.UserType;
import fr.openobservatory.backend.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@AllArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registerAccount(RegisterUserDto accountDto) throws UsernameAlreadyUsedException {
        if (userRepository.findByUsername(accountDto.getUsername()).isPresent()) {
            throw new UsernameAlreadyUsedException();
        }
        User user = new User();
        user.setUsername(accountDto.getUsername());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setBiography(accountDto.getBiography());
        user.setPublicProfil(true);
        user.setType(UserType.USER);
        user.setAvatarUrl("test");
        user.setCreated_at(new Date());
        return userRepository.save(user);
    }

    public Boolean canUserAuthenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            var userPassword = user.get().getPassword();
            return passwordEncoder.matches(password, userPassword);
        }
        return false;
    }

}
