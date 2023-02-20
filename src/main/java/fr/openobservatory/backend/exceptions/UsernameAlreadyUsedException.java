package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
    code = HttpStatus.CONFLICT,
    reason = "USERNAME_ALREADY_USED")
public class UsernameAlreadyUsedException extends RuntimeException {}
