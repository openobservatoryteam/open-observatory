package fr.openobservatory.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.FORBIDDEN, reason = "NOT AUTHORIZED TO SEE PROFILE")
public class ProfileNotAccessibleException extends RuntimeException {}
