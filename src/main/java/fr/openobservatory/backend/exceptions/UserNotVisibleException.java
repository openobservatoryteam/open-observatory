package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "PROFILE_NOT_VISIBLE")
public class UserNotVisibleException extends RuntimeException {}
